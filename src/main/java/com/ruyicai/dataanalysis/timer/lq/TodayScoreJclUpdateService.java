package com.ruyicai.dataanalysis.timer.lq;

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

import com.ruyicai.dataanalysis.consts.lq.MatchStateJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;

/**
 * 竞彩篮球-今日比分更新
 * @author Administrator
 *
 */
@Service
public class TodayScoreJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(TodayScoreJclUpdateService.class);
	
	@Value("${todayScoreJclUrl}")
	private String todayScoreJclUrl;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("竞彩篮球-今日比分更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.downfile(todayScoreJclUrl, HttpUtil.GBK);
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-今日比分更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().element("m").elements("h");
			for(Element match : matches) {
				doProcess(match);
			}
		} catch (DocumentException e) {
			logger.error("竞彩篮球-今日比分更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-今日比分更新结束,共用时 {}", new Long[] {endmillis - startmillis});
	}

	/**
	 * 解析数据
	 * @param match
	 */
	private void doProcess(Element match) {
		try {
			String info = match.getText();
			String[] infos = info.split("\\^");
			String scheduleId = infos[0]; //赛事ID
			String sclassName = infos[1]; //联赛名(如 NBA,WNBA)
			String sclassNameJs = sclassName.split(",")[0]; //联赛名(简体简称)
			String sclassType = infos[2]; //分几节进行(2:上下半场;4:分4小节)
			String matchState = infos[5]; //状态
			String remainTime = infos[6]; //小节剩余时间
			String homeTeamId = infos[7]; //主队ID
			String homeTeam = infos[8].indexOf(",")>-1?infos[8].split(",")[0]:""; //主队名
			String guestTeamId = infos[9]; //客队ID
			String guestTeam = infos[10].indexOf(",")>-1?infos[10].split(",")[0]:""; //客队名
			String homeScore = infos[11]; //主队得分
			String guestScore = infos[12]; //客队得分
			String homeOne = infos[13]; //主队一节得分(上半场)
			String guestOne = infos[14]; //客队一节得分（上半场）
			String homeTwo = infos[15]; //主队二节得分
			String guestTwo = infos[16]; //客队二节得分
			String homeThree = infos[17]; //主队三节得分(下半场）
			String guestThree = infos[18]; //客队三节得分(下半场）
			String homeFour = infos[19]; //主队四节得分
			String guestFour = infos[20]; //客队四节得分
			String addTime = infos[21]; //加时数
			String homeAddTime1 = infos[22]; //主队1'ot得分
			String guestAddTime1 = infos[23]; //客队1'ot得分
			String homeAddTime2 = infos[24]; //主队2'ot得分
			String guestAddTime2 = infos[25]; //客队2'ot得分
			String homeAddTime3 = infos[26]; //主队3'ot得分
			String guestAddTime3 = infos[27]; //客队3'ot得分
			
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJclNotBuild(Integer.parseInt(scheduleId));
			if(null == scheduleJcl) {
				//logger.info("竞彩篮球今日比分更新时ScheduleJcl表无记录,scheduleId="+scheduleId);
				return;
			}
			boolean isModify = false; //是否变化
			boolean scoreModify = false;
			
			String sclassNameJs_old = scheduleJcl.getSclassNameJs();
			if (!StringUtil.isEmpty(sclassNameJs) && (StringUtil.isEmpty(sclassNameJs_old)||!sclassNameJs.equals(sclassNameJs_old))) {
				isModify = true;
				scheduleJcl.setSclassNameJs(sclassNameJs);
			}
			
			String sclassType_old = scheduleJcl.getSclassType();
			if (!StringUtil.isEmpty(sclassType) && (StringUtil.isEmpty(sclassType_old)||!sclassType.equals(sclassType_old))) {
				isModify = true;
				scheduleJcl.setSclassType(sclassType);
			}
			
			String matchState_old = scheduleJcl.getMatchState(); //之前的比赛状态
			if (!StringUtil.isEmpty(matchState) && (StringUtil.isEmpty(matchState_old)||!matchState.equals(matchState_old))) {
				isModify = true;
				scheduleJcl.setMatchState(matchState);
			}
			
			String remainTime_old = scheduleJcl.getRemainTime();
			if (!StringUtil.isEmpty(remainTime) && (StringUtil.isEmpty(remainTime_old)||!remainTime.equals(remainTime_old))) {
				isModify = true;
				scheduleJcl.setRemainTime(remainTime);
			}
			
			String homeTeamId_old = scheduleJcl.getHomeTeamId();
			if (!StringUtil.isEmpty(homeTeamId) && (StringUtil.isEmpty(homeTeamId_old)||!homeTeamId.equals(homeTeamId_old))) {
				isModify = true;
				scheduleJcl.setHomeTeamId(homeTeamId);
			}
			
			String homeTeam_old = scheduleJcl.getHomeTeam();
			if (!StringUtil.isEmpty(homeTeam) && (StringUtil.isEmpty(homeTeam_old)||!homeTeam.equals(homeTeam_old))) {
				isModify = true;
				scheduleJcl.setHomeTeam(homeTeam);
			}
			
			String guestTeamId_old = scheduleJcl.getGuestTeamId();
			if (!StringUtil.isEmpty(guestTeamId) && (StringUtil.isEmpty(guestTeamId_old)||!guestTeamId.equals(guestTeamId_old))) {
				isModify = true;
				scheduleJcl.setGuestTeamId(guestTeamId);
			}
			
			String guestTeam_old = scheduleJcl.getGuestTeam();
			if (!StringUtil.isEmpty(guestTeam) && (StringUtil.isEmpty(guestTeam_old)||!guestTeam.equals(guestTeam_old))) {
				isModify = true;
				scheduleJcl.setGuestTeam(guestTeam);
			}
			//主队比分
			String homeScore_old = scheduleJcl.getHomeScore();
			if (!StringUtils.equals(homeScore_old, NumberUtil.parseString(homeScore, "0"))) {
				isModify = true;
				scoreModify = true;
				scheduleJcl.setHomeScore(NumberUtil.parseString(homeScore, "0"));
			}
			//客队比分
			String guestScore_old = scheduleJcl.getGuestScore();
			if (!StringUtils.equals(guestScore_old, NumberUtil.parseString(guestScore, "0"))) {
				isModify = true;
				scoreModify = true;
				scheduleJcl.setGuestScore(NumberUtil.parseString(guestScore, "0"));
			}
			//主队第一节比分
			String homeOne_old = scheduleJcl.getHomeOne();
			if (!StringUtils.equals(homeOne_old, NumberUtil.parseString(homeOne, "0"))) {
				isModify = true;
				scheduleJcl.setHomeOne(NumberUtil.parseString(homeOne, "0"));
			}
			//客队第一节比分
			String guestOne_old = scheduleJcl.getGuestOne();
			if (!StringUtils.equals(guestOne_old, NumberUtil.parseString(guestOne, "0"))) {
				isModify = true;
				scheduleJcl.setGuestOne(NumberUtil.parseString(guestOne, "0"));
			}
			//主队第二节比分
			String homeTwo_old = scheduleJcl.getHomeTwo();
			if (!StringUtils.equals(homeTwo_old, NumberUtil.parseString(homeTwo, "0"))) {
				isModify = true;
				scheduleJcl.setHomeTwo(NumberUtil.parseString(homeTwo, "0"));
			}
			//客队第二节比分
			String guestTwo_old = scheduleJcl.getGuestTwo();
			if (!StringUtils.equals(guestTwo_old, NumberUtil.parseString(guestTwo, "0"))) {
				isModify = true;
				scheduleJcl.setGuestTwo(NumberUtil.parseString(guestTwo, "0"));
			}
			//主队第三节比分
			String homeThree_old = scheduleJcl.getHomeThree();
			if (!StringUtils.equals(homeThree_old, NumberUtil.parseString(homeThree, "0"))) {
				isModify = true;
				scheduleJcl.setHomeThree(NumberUtil.parseString(homeThree, "0"));
			}
			//客队第三节比分
			String guestThree_old = scheduleJcl.getGuestThree();
			if (!StringUtils.equals(guestThree_old, NumberUtil.parseString(guestThree, "0"))) {
				isModify = true;
				scheduleJcl.setGuestThree(NumberUtil.parseString(guestThree, "0"));
			}
			//主队第四节比分
			String homeFour_old = scheduleJcl.getHomeFour();
			if (!StringUtils.equals(homeFour_old, NumberUtil.parseString(homeFour, "0"))) {
				isModify = true;
				scheduleJcl.setHomeFour(NumberUtil.parseString(homeFour, "0"));
			}
			//客队第四节比分
			String guestFour_old = scheduleJcl.getGuestFour();
			if (!StringUtils.equals(guestFour_old, NumberUtil.parseString(guestFour, "0"))) {
				isModify = true;
				scheduleJcl.setGuestFour(NumberUtil.parseString(guestFour, "0"));
			}
			
			String addTime_old = scheduleJcl.getAddTime();
			if (!StringUtil.isEmpty(addTime) && (StringUtil.isEmpty(addTime_old)||!addTime.equals(addTime_old))) {
				isModify = true;
				scheduleJcl.setAddTime(addTime);
			}
			//主队加时1比分
			String homeAddTime1_old = scheduleJcl.getHomeAddTime1();
			if (!StringUtils.equals(homeAddTime1_old, NumberUtil.parseString(homeAddTime1, "0"))) {
				isModify = true;
				scheduleJcl.setHomeAddTime1(NumberUtil.parseString(homeAddTime1, "0"));
			}
			//客队加时1比分
			String guestAddTime1_old = scheduleJcl.getGuestAddTime1();
			if (!StringUtils.equals(guestAddTime1_old, NumberUtil.parseString(guestAddTime1, "0"))) {
				isModify = true;
				scheduleJcl.setGuestAddTime1(NumberUtil.parseString(guestAddTime1, "0"));
			}
			//主队加时2比分
			String homeAddTime2_old = scheduleJcl.getHomeAddTime2();
			if (!StringUtils.equals(homeAddTime2_old, NumberUtil.parseString(homeAddTime2, "0"))) {
				isModify = true;
				scheduleJcl.setHomeAddTime2(NumberUtil.parseString(homeAddTime2, "0"));
			}
			//客队加时2比分
			String guestAddTime2_old = scheduleJcl.getGuestAddTime2();
			if (!StringUtils.equals(guestAddTime2_old, NumberUtil.parseString(guestAddTime2, "0"))) {
				isModify = true;
				scheduleJcl.setGuestAddTime2(NumberUtil.parseString(guestAddTime2, "0"));
			}
			//主队加时3比分
			String homeAddTime3_old = scheduleJcl.getHomeAddTime3();
			if (!StringUtils.equals(homeAddTime3_old, NumberUtil.parseString(homeAddTime3, "0"))) {
				isModify = true;
				scheduleJcl.setHomeAddTime3(NumberUtil.parseString(homeAddTime3, "0"));
			}
			//客队加时3比分
			String guestAddTime3_old = scheduleJcl.getGuestAddTime3();
			if (!StringUtils.equals(guestAddTime3_old, NumberUtil.parseString(guestAddTime3, "0"))) {
				isModify = true;
				scheduleJcl.setGuestAddTime3(NumberUtil.parseString(guestAddTime3, "0"));
			}
			
			if (isModify) {
				scheduleJcl.merge();
				if (StringUtils.equals(scheduleJcl.getMatchState(), MatchStateJcl.wanChang.value())) { //已完场
					if (!StringUtils.equals(matchState_old, MatchStateJcl.wanChang.value())) { //之前的状态不是完场
						String event = scheduleJcl.getEvent();
						if (StringUtils.isNotBlank(event)) {
							sendJmsJclUtil.sendScheduleFinishJms(event, scheduleJcl); //发送完场的Jms
						}
						sendJmsJclUtil.sendRankingUpdateJms(scheduleJcl.getScheduleId()); //更新排名
					}
					//处理完场后比分发生变化的情况(球探网的比分错误,之后人工修改正确)
					if (StringUtils.equals(matchState_old, MatchStateJcl.wanChang.value()) && scoreModify) {
						String event = scheduleJcl.getEvent();
						if (StringUtils.isNotBlank(event)) { 
							sendJmsJclUtil.sendScoreModifyJms(event, scheduleJcl); //发送比分变化的Jms
						}
						sendJmsJclUtil.sendRankingUpdateJms(scheduleJcl.getScheduleId()); //更新排名
					}
				}
				//发送赛事缓存更新的Jms
				sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
			}
		} catch (Exception e) {
			logger.error("解析竞彩篮球-今日比分异常", e);
		}
	}
	
}
