package com.ruyicai.dataanalysis.timer.lq;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;
import com.ruyicai.dataanalysis.util.jc.JmsSendUtil;

/**
 * 竞彩篮球-彩票赛事与球探网的关联更新
 * @author Administrator
 *
 */
@Service
public class QiuTanMatchesJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(QiuTanMatchesJclUpdateService.class);

	@Value("${qiutanmatches}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private JmsSendUtil jmsSendUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("竞彩篮球-彩票赛事与球探网的关联更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-彩票赛事与球探网的关联更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("i");
			logger.info("竞彩篮球-彩票赛事与球探网的关联更新 size:" + matches.size());
			for(Element match : matches) {
				doProcess(match);
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-彩票赛事与球探网的关联更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-彩票赛事与球探网的关联更新结束，共用时 " + (endmillis - startmillis));
	}
	
	/**
	 * 解析数据
	 * @param match
	 */
	private void doProcess(Element match) {
		try {
			String lotteryName = match.elementTextTrim("LotteryName"); //彩种名称
			String issueNum = match.elementTextTrim("IssueNum"); //期号(竞彩为空)
			String iD = match.elementTextTrim("ID"); //场次
			String iD_bet007 = match.elementTextTrim("ID_bet007"); //球探网比赛ID
			String time = match.elementTextTrim("time"); //比赛时间
			String home = match.elementTextTrim("Home"); //主队
			String away = match.elementTextTrim("Away"); //客队
			String homeID = match.elementTextTrim("HomeID"); //主队id
			String awayID = match.elementTextTrim("AwayID"); //客队id
			String turn = match.elementTextTrim("Turn"); //主客队是否需要反转
			turn = StringUtils.equals(turn, "True") ? "1" : "0";
			//如果不是竞彩篮球则不执行
			if (!JingCaiUtil.isJcLq(lotteryName)) {
				return;
			}
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(Integer.parseInt(iD_bet007));
			if(scheduleJcl==null) {
				return;
			}
			QiuTanMatches qiuTanMatches = QiuTanMatches.findByID_bet007(Integer.parseInt(iD_bet007), lotteryName);
			if(qiuTanMatches==null) { //记录不存在
				qiuTanMatches = new QiuTanMatches();
				qiuTanMatches.setLotteryName(lotteryName);
				qiuTanMatches.setIssueNum(issueNum);
				qiuTanMatches.setId(iD);
				qiuTanMatches.setID_bet007(Integer.parseInt(iD_bet007));
				qiuTanMatches.setTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", time));
				qiuTanMatches.setHome(home);
				qiuTanMatches.setAway(away);
				qiuTanMatches.setHomeID(Integer.parseInt(homeID));
				qiuTanMatches.setAwayID(Integer.parseInt(awayID));
				qiuTanMatches.setTurn(turn);
				//设置event
				String event = JingCaiUtil.getEvent(lotteryName, iD, qiuTanMatches.getTime());
				qiuTanMatches.setEvent(event);
				//判断是否有重复赛事
				JingCaiUtil.jclProcess(event, iD_bet007);
 				qiuTanMatches.persist();
			} else { //记录已存在
				boolean isModify = false;
				
				String lotteryName_old = qiuTanMatches.getLotteryName();
				if (!StringUtil.isEmpty(lotteryName) && (StringUtil.isEmpty(lotteryName_old)||!lotteryName.equals(lotteryName_old))) {
					isModify = true;
					qiuTanMatches.setLotteryName(lotteryName);
				}
				String issueNum_old = qiuTanMatches.getIssueNum();
				if (!StringUtil.isEmpty(issueNum) && (StringUtil.isEmpty(issueNum_old)||!issueNum.equals(issueNum_old))) {
					isModify = true;
					qiuTanMatches.setIssueNum(issueNum);
				}
				String id_old = qiuTanMatches.getId();
				if (!StringUtil.isEmpty(iD) && (StringUtil.isEmpty(id_old)||!iD.equals(id_old))) {
					isModify = true;
					qiuTanMatches.setId(iD);
				}
				Integer id_bet0072_old = qiuTanMatches.getID_bet007();
				if (!StringUtil.isEmpty(iD_bet007) && (id_bet0072_old==null||Integer.parseInt(iD_bet007)!=id_bet0072_old)) {
					isModify = true;
					qiuTanMatches.setID_bet007(Integer.parseInt(iD_bet007));
				}
				Date time_old = qiuTanMatches.getTime();
				if (StringUtils.isNotBlank(time)) {
					String pattern = "yyyy/MM/dd HH:mm:ss";
					Date dateTime = DateUtil.parse(pattern, time);
					String dateTimeStr = DateUtil.format(pattern, dateTime);
					String time_oldStr = DateUtil.format(pattern, time_old);
					if (time_old==null||!StringUtils.equals(dateTimeStr, time_oldStr)) {
						isModify = true;
						qiuTanMatches.setTime(dateTime);
					}
				}
				String home_old = qiuTanMatches.getHome();
				if (!StringUtil.isEmpty(home) && (StringUtil.isEmpty(home_old)||!home.equals(home_old))) {
					isModify = true;
					qiuTanMatches.setHome(home);
				}
				String away_old = qiuTanMatches.getAway();
				if (!StringUtil.isEmpty(away) && (StringUtil.isEmpty(away_old)||!away.equals(away_old))) {
					isModify = true;
					qiuTanMatches.setAway(away);
				}
				Integer homeID_old = qiuTanMatches.getHomeID();
				if (!StringUtil.isEmpty(homeID) && (homeID_old==null||Integer.parseInt(homeID)!=homeID_old)) {
					isModify = true;
					qiuTanMatches.setHomeID(Integer.parseInt(homeID));
				}
				Integer awayID_old = qiuTanMatches.getAwayID();
				if (!StringUtil.isEmpty(awayID) && (awayID_old==null||Integer.parseInt(awayID)!=awayID_old)) {
					isModify = true;
					qiuTanMatches.setAwayID(Integer.parseInt(awayID));
				}
				//设置event
				String event = JingCaiUtil.getEvent(lotteryName, iD, qiuTanMatches.getTime());
				String event_old = qiuTanMatches.getEvent();
				if (!StringUtil.isEmpty(event) && (StringUtil.isEmpty(event_old)||!event.equals(event_old))) {
					isModify = true;
					qiuTanMatches.setEvent(event);
				}
				//设置turn
				String turn_old = qiuTanMatches.getTurn();
				if (StringUtils.isNotBlank(turn) && (StringUtils.isBlank(turn_old)||!StringUtils.equals(turn, turn_old))) {
					isModify = true;
					qiuTanMatches.setTurn(turn);
				}
				//判断是否有重复赛事
				JingCaiUtil.jclProcess(event, iD_bet007);
				if (isModify) {
					qiuTanMatches.merge();
				}
			}
			//更新赛事event
			updateScheduleEvent(qiuTanMatches, scheduleJcl);
		} catch(Exception e) {
			logger.error("竞彩篮球-解析彩票赛事与球探网的关联发生异常", e);
		}
	}
	
	/**
	 * 更新赛事的event
	 * @param qiuTanMatches
	 * @param schedule
	 */
	private void updateScheduleEvent(QiuTanMatches qiuTanMatches, ScheduleJcl scheduleJcl) {
		boolean isUpdate = false;
		boolean eventModify = false;
		String eventQ = qiuTanMatches.getEvent();
		String eventS = scheduleJcl.getEvent();
		if(!StringUtil.isEmpty(eventQ)&&(StringUtil.isEmpty(eventS)||!eventQ.equals(eventS))) {
			logger.info("赛事添加event,event="+eventQ+",scheduleId="+scheduleJcl.getScheduleId());
			isUpdate = true;
			scheduleJcl.setEvent(eventQ);
			eventModify = true;
		}
		String turnQ = qiuTanMatches.getTurn();
		String turnS = scheduleJcl.getTurn();
		if (StringUtils.isNotBlank(turnQ)&&(StringUtils.isBlank(turnS)||!StringUtils.equals(turnQ, turnS))) {
			isUpdate = true;
			scheduleJcl.setTurn(turnQ);
		}
		if (isUpdate) {
			scheduleJcl.merge();
		}
		//发送event增加的Jms
		if (eventModify && StringUtils.isNotBlank(scheduleJcl.getEvent())) {
			jmsSendUtil.scheduleEventAdd(scheduleJcl.getEvent());
		}
	}
	
}
