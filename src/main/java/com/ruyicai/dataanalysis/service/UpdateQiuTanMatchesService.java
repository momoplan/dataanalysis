package com.ruyicai.dataanalysis.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.QiuTanMatches;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.BeiDanUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.FileUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.JingCaiUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ZuCaiUtil;

@Service
public class UpdateQiuTanMatchesService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateQiuTanMatchesService.class);

	@Value("${qiutanmatches}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	public void processFile(String filename) {
		logger.info("开始读取文件更新彩票赛事与球探网的关联表, filename:{}", new String[] {filename});
		long startmillis = System.currentTimeMillis();
		try {
			String data = FileUtil.read(filename);
			if (StringUtil.isEmpty(data)) {
				logger.info("更新彩票赛事与球探网的关联表时获取数据为空");
				return;
			}
			doProcess(data);
		} catch(Exception e) {
			logger.error("读取文件更新彩票赛事与球探网的关联表出错,filename:" + filename, e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("读取文件更新彩票赛事与球探网的关联表结束，filename:{}, 共用时 ", new String[] {filename, String.valueOf((endmillis - startmillis))});
	}
	
	public void process() {
		logger.info("开始更新彩票赛事与球探网的关联表");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			doProcess(data);
		} catch(Exception e) {
			logger.error("更新彩票赛事与球探网的关联表出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新彩票赛事与球探网的关联表结束，共用时 " + (endmillis - startmillis));
	}

	@SuppressWarnings("unchecked")
	private void doProcess(String data) throws DocumentException {
		Document doc = DocumentHelper.parseText(data);
		List<Element> matches = doc.getRootElement().elements("i");
		logger.info("彩票赛事与球探网的关联表 size:" + matches.size());
		for(Element match : matches) {
			doProcess(match);
		}
	}

	/**
	 * 解析数据
	 * @param match
	 */
	private void doProcess(Element match) {
		try {
			String lotteryName = match.elementTextTrim("LotteryName");
			String issueNum = match.elementTextTrim("IssueNum");
			String iD = match.elementTextTrim("ID");
			String iD_bet007 = match.elementTextTrim("ID_bet007");
			String time = match.elementTextTrim("time");
			String home = match.elementTextTrim("Home");
			String away = match.elementTextTrim("Away");
			String homeID = match.elementTextTrim("HomeID");
			String awayID = match.elementTextTrim("AwayID");
			//如果是竞彩篮球则不执行
			if (JingCaiUtil.isJcLq(lotteryName)) { //竞彩篮球
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(iD_bet007));
			if(null == schedule) {
				return;
			}
			QiuTanMatches qiuTanMatches = QiuTanMatches.findByID_bet007(Integer.parseInt(iD_bet007), lotteryName);
			if(null == qiuTanMatches) { //记录不存在
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
				//设置event
				if (JingCaiUtil.isJcZq(lotteryName)) { //竞彩足球
					String event = JingCaiUtil.getEvent(lotteryName, iD, qiuTanMatches.getTime());
					qiuTanMatches.setEvent(event);
				} else if (ZuCaiUtil.isZuCai(lotteryName)) { //足彩
					String zcEvent = ZuCaiUtil.getZcEvent(lotteryName, issueNum, iD);
					if (ZuCaiUtil.isZcSfc(lotteryName)) { //足彩胜负彩
						qiuTanMatches.setZcSfcEvent(zcEvent);
					} else if (ZuCaiUtil.isZcJqc(lotteryName)) { //足彩进球彩
						qiuTanMatches.setZcJqcEvent(zcEvent);
					} else if (ZuCaiUtil.isZcBqc(lotteryName)) { //足彩半全场
						qiuTanMatches.setZcBqcEvent(zcEvent);
					}
					//足彩处理
					ZuCaiUtil.zcProcess(lotteryName, issueNum, iD, iD_bet007);
				} else if (BeiDanUtil.isBeiDan(lotteryName)) { //北单
					String bdEvent = BeiDanUtil.getBdEvent(issueNum, iD);
					qiuTanMatches.setBdEvent(bdEvent);
				}
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
				if (!StringUtil.isEmpty(time) && (time_old==null||!time.equals(DateUtil.format("yyyy/MM/dd HH:mm:ss", time_old)))) {
					isModify = true;
					qiuTanMatches.setTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", time));
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
				if (JingCaiUtil.isJcZq(lotteryName)) { //竞彩足球
					String event = JingCaiUtil.getEvent(lotteryName, iD, qiuTanMatches.getTime());
					String event_old = qiuTanMatches.getEvent();
					if (!StringUtil.isEmpty(event) && (StringUtil.isEmpty(event_old)||!event.equals(event_old))) {
						isModify = true;
						qiuTanMatches.setEvent(event);
					}
				} else if (ZuCaiUtil.isZuCai(lotteryName)) { //足彩
					String zcEvent = ZuCaiUtil.getZcEvent(lotteryName, issueNum, iD);
					if (ZuCaiUtil.isZcSfc(lotteryName)) { //足彩胜负彩
						String zcSfcEvent_old = qiuTanMatches.getZcSfcEvent();
						if (!StringUtil.isEmpty(zcEvent) && (StringUtil.isEmpty(zcSfcEvent_old)||!zcEvent.equals(zcSfcEvent_old))) {
							isModify = true;
							qiuTanMatches.setZcSfcEvent(zcEvent);
						}
					} else if (ZuCaiUtil.isZcJqc(lotteryName)) { //足彩进球彩
						String zcJqcEvent_old = qiuTanMatches.getZcJqcEvent();
						if (!StringUtil.isEmpty(zcEvent) && (StringUtil.isEmpty(zcJqcEvent_old)||!zcEvent.equals(zcJqcEvent_old))) {
							isModify = true;
							qiuTanMatches.setZcJqcEvent(zcEvent);
						}
					} else if (ZuCaiUtil.isZcBqc(lotteryName)) { //足彩半全场
						String zcBqcEvent_old = qiuTanMatches.getZcBqcEvent();
						if (!StringUtil.isEmpty(zcEvent) && (StringUtil.isEmpty(zcBqcEvent_old)||!zcEvent.equals(zcBqcEvent_old))) {
							isModify = true;
							qiuTanMatches.setZcBqcEvent(zcEvent);
						}
					}
					//足彩处理
					ZuCaiUtil.zcProcess(lotteryName, issueNum, iD, iD_bet007);
				} else if (BeiDanUtil.isBeiDan(lotteryName)) { //北单
					String bdEvent = BeiDanUtil.getBdEvent(issueNum, iD);
					String bdEvent_old = qiuTanMatches.getBdEvent();
					if (StringUtils.isNotBlank(bdEvent) && (StringUtils.isBlank(bdEvent_old)||!StringUtils.equals(bdEvent, bdEvent_old))) {
						isModify = true;
						qiuTanMatches.setBdEvent(bdEvent);
					}
				}
				if (isModify) {
					qiuTanMatches.merge();
				}
			}
			//更新赛事的event
			updateScheduleEvent(qiuTanMatches, schedule);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 更新赛事的event
	 * @param qiuTanMatches
	 * @param schedule
	 */
	private void updateScheduleEvent(QiuTanMatches qiuTanMatches, Schedule schedule) {
		boolean isUpdate = false;
		//竞彩足球
		String eventQ = qiuTanMatches.getEvent();
		String eventS = schedule.getEvent();
		if(!StringUtil.isEmpty(eventQ)&&(StringUtil.isEmpty(eventS)||!eventQ.equals(eventS))) {
			schedule.setEvent(eventQ);
			isUpdate = true;
		}
		//足彩胜负彩
		String zcSfcEventQ = qiuTanMatches.getZcSfcEvent();
		String zcSfcEventS = schedule.getZcSfcEvent();
		if(!StringUtil.isEmpty(zcSfcEventQ)&&(StringUtil.isEmpty(zcSfcEventS)||!zcSfcEventQ.equals(zcSfcEventS))) {
			schedule.setZcSfcEvent(zcSfcEventQ);
			isUpdate = true;
		}
		//足彩进球彩
		String zcJqcEventQ = qiuTanMatches.getZcJqcEvent();
		String zcJqcEventS = schedule.getZcJqcEvent();
		if(!StringUtil.isEmpty(zcJqcEventQ)&&(StringUtil.isEmpty(zcJqcEventS)||!zcJqcEventQ.equals(zcJqcEventS))) {
			schedule.setZcJqcEvent(zcJqcEventQ);
			isUpdate = true;
		}
		//足彩半全场
		String zcBqcEventQ = qiuTanMatches.getZcBqcEvent();
		String zcBqcEventS = schedule.getZcBqcEvent();
		if(!StringUtil.isEmpty(zcBqcEventQ)&&(StringUtil.isEmpty(zcBqcEventS)||!zcBqcEventQ.equals(zcBqcEventS))) {
			schedule.setZcBqcEvent(zcBqcEventQ);
			isUpdate = true;
		}
		//北单
		String bdEventQ = qiuTanMatches.getBdEvent();
		String bdEventS = schedule.getBdEvent();
		if(StringUtils.isNotBlank(bdEventQ)&&(StringUtils.isBlank(bdEventS)||!StringUtils.equals(bdEventQ, bdEventS))) {
			schedule.setZcBqcEvent(bdEventQ);
			isUpdate = true;
		}
		if (isUpdate) {
			schedule.merge();
		}
	}
	
}
