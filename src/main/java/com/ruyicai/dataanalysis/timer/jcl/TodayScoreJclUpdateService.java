package com.ruyicai.dataanalysis.timer.jcl;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.jcl.MatchStateJcl;
import com.ruyicai.dataanalysis.domain.jcl.ScheduleJcl;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jcl.SendJmsJclUtil;

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
			
			String homeScore_old = scheduleJcl.getHomeScore();
			if (!StringUtil.isEmpty(homeScore) && (StringUtil.isEmpty(homeScore_old)||!homeScore.equals(homeScore_old))) {
				isModify = true;
				scheduleJcl.setHomeScore(homeScore);
			}
			
			String guestScore_old = scheduleJcl.getGuestScore();
			if (!StringUtil.isEmpty(guestScore) && (StringUtil.isEmpty(guestScore_old)||!guestScore.equals(guestScore_old))) {
				isModify = true;
				scheduleJcl.setGuestScore(guestScore);
			}
			
			String homeOne_old = scheduleJcl.getHomeOne();
			if (!StringUtil.isEmpty(homeOne) && (StringUtil.isEmpty(homeOne_old)||!homeOne.equals(homeOne_old))) {
				isModify = true;
				scheduleJcl.setHomeOne(homeOne);
			}
			
			String guestOne_old = scheduleJcl.getGuestOne();
			if (!StringUtil.isEmpty(guestOne) && (StringUtil.isEmpty(guestOne_old)||!homeOne.equals(guestOne_old))) {
				isModify = true;
				scheduleJcl.setGuestOne(guestOne);
			}
			
			String homeTwo_old = scheduleJcl.getHomeTwo();
			if (!StringUtil.isEmpty(homeTwo) && (StringUtil.isEmpty(homeTwo_old)||!homeTwo.equals(homeTwo_old))) {
				isModify = true;
				scheduleJcl.setHomeTwo(homeTwo);
			}
			
			String guestTwo_old = scheduleJcl.getGuestTwo();
			if (!StringUtil.isEmpty(guestTwo) && (StringUtil.isEmpty(guestTwo_old)||!guestTwo.equals(guestTwo_old))) {
				isModify = true;
				scheduleJcl.setGuestTwo(guestTwo);
			}
			
			String homeThree_old = scheduleJcl.getHomeThree();
			if (!StringUtil.isEmpty(homeThree) && (StringUtil.isEmpty(homeThree_old)||!homeThree.equals(homeThree_old))) {
				isModify = true;
				scheduleJcl.setHomeThree(homeThree);
			}
			
			String guestThree_old = scheduleJcl.getGuestThree();
			if (!StringUtil.isEmpty(guestThree) && (StringUtil.isEmpty(guestThree_old)||!guestThree.equals(guestThree_old))) {
				isModify = true;
				scheduleJcl.setGuestThree(guestThree);
			}
			
			String homeFour_old = scheduleJcl.getHomeFour();
			if (!StringUtil.isEmpty(homeFour) && (StringUtil.isEmpty(homeFour_old)||!homeFour.equals(homeFour_old))) {
				isModify = true;
				scheduleJcl.setHomeFour(homeFour);
			}
			
			String guestFour_old = scheduleJcl.getGuestFour();
			if (!StringUtil.isEmpty(guestFour) && (StringUtil.isEmpty(guestFour_old)||!guestFour.equals(guestFour_old))) {
				isModify = true;
				scheduleJcl.setGuestFour(guestFour);
			}
			
			String addTime_old = scheduleJcl.getAddTime();
			if (!StringUtil.isEmpty(addTime) && (StringUtil.isEmpty(addTime_old)||!addTime.equals(addTime_old))) {
				isModify = true;
				scheduleJcl.setAddTime(addTime);
			}
			
			String homeAddTime1_old = scheduleJcl.getHomeAddTime1();
			if (!StringUtil.isEmpty(homeAddTime1) && (StringUtil.isEmpty(homeAddTime1_old)||!homeAddTime1.equals(homeAddTime1_old))) {
				isModify = true;
				scheduleJcl.setHomeAddTime1(homeAddTime1);
			}
			
			String guestAddTime1_old = scheduleJcl.getGuestAddTime1();
			if (!StringUtil.isEmpty(guestAddTime1) && (StringUtil.isEmpty(guestAddTime1_old)||!guestAddTime1.equals(guestAddTime1_old))) {
				isModify = true;
				scheduleJcl.setGuestAddTime1(guestAddTime1);
			}
			
			String homeAddTime2_old = scheduleJcl.getHomeAddTime2();
			if (!StringUtil.isEmpty(homeAddTime2) && (StringUtil.isEmpty(homeAddTime2_old)||!homeAddTime2.equals(homeAddTime2_old))) {
				isModify = true;
				scheduleJcl.setHomeAddTime2(homeAddTime2);
			}
			
			String guestAddTime2_old = scheduleJcl.getGuestAddTime2();
			if (!StringUtil.isEmpty(guestAddTime2) && (StringUtil.isEmpty(guestAddTime2_old)||!guestAddTime2.equals(guestAddTime2_old))) {
				isModify = true;
				scheduleJcl.setGuestAddTime2(guestAddTime2);
			}
			
			String homeAddTime3_old = scheduleJcl.getHomeAddTime3();
			if (!StringUtil.isEmpty(homeAddTime3) && (StringUtil.isEmpty(homeAddTime3_old)||!homeAddTime3.equals(homeAddTime3_old))) {
				isModify = true;
				scheduleJcl.setHomeAddTime3(homeAddTime3);
			}
			
			String guestAddTime3_old = scheduleJcl.getGuestAddTime3();
			if (!StringUtil.isEmpty(guestAddTime3) && (StringUtil.isEmpty(guestAddTime3_old)||!guestAddTime3.equals(guestAddTime3_old))) {
				isModify = true;
				scheduleJcl.setGuestAddTime3(guestAddTime3);
			}
			
			if (isModify) {
				scheduleJcl.merge();
				//之前没有完场，现在已完场
				String matchState_now = scheduleJcl.getMatchState();
				if (matchState_old!=null && !matchState_old.equals(MatchStateJcl.wanChang.value) 
						&& matchState_now!=null && matchState_now.equals(MatchStateJcl.wanChang.value)) {
					//更新联赛排名
					sendJmsJclUtil.sendRankingUpdateJms(scheduleJcl.getScheduleId());
					//更新让分、总分盘
					String event = scheduleJcl.getEvent();
					if (!StringUtil.isEmpty(event)) {
						sendJmsJclUtil.sendScheduleUpdateJms(event);
					}
				}
			}
		} catch (Exception e) {
			logger.error("解析竞彩篮球-今日比分异常", e);
		}
	}
	
}
