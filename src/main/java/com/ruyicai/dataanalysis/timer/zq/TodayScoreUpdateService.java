package com.ruyicai.dataanalysis.timer.zq;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.zq.JmsZqUtil;

/**
 * 足球-今日比分数据更新
 * @author Administrator
 *
 */
@Service
public class TodayScoreUpdateService {

	private Logger logger = LoggerFactory.getLogger(TodayScoreUpdateService.class);
	
	@Value("${todaybifeng}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private JmsZqUtil jmsZqUtil;
	
	public void process() {
		try {
			logger.info("足球-今日比分数据更新 start");
			long startmillis = System.currentTimeMillis();
			String data = httpUtil.downfile(url, HttpUtil.GBK);
			if (StringUtil.isEmpty(data)) {
				logger.info("足球-今日比分数据更新获取数据为空");
				return;
			}
			String[] datas = data.split("\\;");
			for(int i = 0 ; i < datas.length; i++) {
				String value = datas[i];
				value = value.replaceFirst("^\\s*", "");
				if(value.startsWith("A")) {
					doProcess(value);
				}
			}
			long endmillis = System.currentTimeMillis();
			logger.info("足球-今日比分数据更新end,用时 {}", new Long[] {endmillis - startmillis});
		} catch (Exception e) {
			logger.error("足球-今日比分数据更新发生异常", e);
		}
	}

	private void doProcess(String data) {
		try {
			data = data.replaceFirst("A\\[\\d+\\]=", "");
			data = data.replaceAll("\\.split\\('\\^'\\)", "");
			data = data.replaceAll("\"", "");
			String[] values = data.split("\\^");
			String scheduleIdStr = values[0]; //比赛Id
			String matchTime = values[11]; //比赛时间(只有小时和分数 20:30 格式)
			String matchTime2 = values[12]; //上半场时为开上半场的时间,下半场时为开下半场的时间(js日期时间格式)
			String matchStateStr = values[13]; //比赛状态 (0:未开,1:上半场,2:中场,3:下半场,4:加时,-11:待定,-12:腰斩,-13:中断,-14:推迟,-1:完场，-10取消)
			String homeScoreStr = values[14]; //主队比分
			String guestScoreStr = values[15]; //客队比分
			String homeHalfScoreStr = values[16]; //主队上半场比分
			String guestHalfScoreStr = values[17]; //客队上半场比分
			String home_Red = values[18]; //主队红牌
			String guest_Red = values[19]; //客队红牌
			String home_yellow = values[20]; //主队黄牌
			String guest_Yellow = values[21]; //客队黄牌
			String homeOrder = values[22]; //主队排名
			String guestOrder = values[23]; //客队排名
			/*String explainList = values.length>=43 ? values[42] : ""; //标识是否有加时
			if (StringUtils.isNotBlank(explainList)) {
				logger.info("scheduleId:"+scheduleIdStr+";explainList:"+explainList);
			}*/
			Integer scheduleId = Integer.parseInt(scheduleIdStr); //比赛Id
			Integer matchState = Integer.parseInt(matchStateStr); //比赛状态
			Integer homeScore = NumberUtil.parseInt(homeScoreStr, 0); //主队比分
			Integer guestScore = NumberUtil.parseInt(guestScoreStr, 0); //客队比分
			Integer homeHalfScore = NumberUtil.parseInt(homeHalfScoreStr, 0); //主队上半场比分
			Integer guestHalfScore = NumberUtil.parseInt(guestHalfScoreStr, 0); //客队上半场比分
			Integer homeRed = NumberUtil.parseInt(home_Red, 0); //主队红牌
			Integer guestRed = NumberUtil.parseInt(guest_Red, 0); //客队红牌
			Integer homeYellow = NumberUtil.parseInt(home_yellow, 0); //主队黄牌
			Integer guestYellow = NumberUtil.parseInt(guest_Yellow, 0); //客队黄牌
			
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			if(schedule == null) {
				return;
			}
			boolean ismod = false;
			boolean scoreModify = false;
			int oldMatchState = schedule.getMatchState(); //比赛状态
			if(matchState != oldMatchState) {
				ismod = true;
				schedule.setMatchState(matchState);
			}
			if(homeScore != schedule.getHomeScore()) {
				ismod = true;
				scoreModify = true;
				schedule.setHomeScore(homeScore);
			}
			if(guestScore != schedule.getGuestScore()) {
				ismod = true;
				scoreModify = true;
				schedule.setGuestScore(guestScore);
			}
			if(homeHalfScore != schedule.getHomeHalfScore()) {
				ismod = true;
				scoreModify = true;
				schedule.setHomeHalfScore(homeHalfScore);
			}
			if(guestHalfScore != schedule.getGuestHalfScore()) {
				ismod = true;
				scoreModify = true;
				schedule.setGuestHalfScore(guestHalfScore);
			}
			if(homeRed != schedule.getHome_Red()) {
				ismod = true;
				schedule.setHome_Red(homeRed);
			}
			if(guestRed != schedule.getGuest_Red()) {
				ismod = true;
				schedule.setGuest_Red(guestRed);
			}
			if(homeYellow != schedule.getHome_Yellow()) {
				ismod = true;
				schedule.setHome_Yellow(homeYellow);
			}
			if(guestYellow != schedule.getGuest_Yellow()) {
				ismod = true;
				schedule.setGuest_Yellow(guestYellow);
			}
			if(StringUtils.isNotBlank(homeOrder) && !homeOrder.equals(schedule.getHome_Order())) {
				ismod = true;
				schedule.setHome_Order(homeOrder);
			}
			if(StringUtils.isNotBlank(guestOrder) && !guestOrder.equals(schedule.getGuest_Order())) {
				ismod = true;
				schedule.setGuest_Order(guestOrder);
			}
			//解析开赛时间
			Date oldMatchTime = schedule.getMatchTime();
			Map<String, String> matchTimeMap = getMatchTimeMap(oldMatchTime, matchTime, matchTime2);
			matchTime = matchTimeMap.get("matchTime");
			matchTime2 = matchTimeMap.get("matchTime2");
			//开赛时间
			if (StringUtils.isNotBlank(matchTime)&&(oldMatchTime==null
					||!StringUtils.equals(matchTime, DateUtil.format("yyyy-MM-dd HH:mm:ss", oldMatchTime)))) {
				ismod = true;
				schedule.setMatchTime(DateUtil.parse("yyyy-MM-dd HH:mm:ss", matchTime));
			}
			//上下半场开始时间
			Date oldMatchTime2 = schedule.getMatchTime2();
			if (StringUtils.isNotBlank(matchTime2)) {
				String pattern = "yyyy-MM-dd HH:mm:ss";
				Date matchTime2Date = DateUtil.parse(pattern, matchTime2);
				String matchTime2DateStr = DateUtil.format(pattern, matchTime2Date);
				String oldMatchTime2Str = DateUtil.format(pattern, oldMatchTime2);
				if (oldMatchTime2==null||!StringUtils.equals(matchTime2DateStr, oldMatchTime2Str)) {
					ismod = true;
					schedule.setMatchTime2(matchTime2Date);
				}
			}
			//是否有加时
			String oldIsAddTime = schedule.getIsAddTime(); //是否有加时(0:无;1:有)
			if ((StringUtils.isBlank(oldIsAddTime)||StringUtils.equals(oldIsAddTime, "0"))
					&&matchState==MatchState.JIASHI.value) {
				schedule.setIsAddTime("1");
			}
			if(ismod) {
				schedule.merge();
				int wanChangS = MatchState.WANCHANG.value; //完场状态
				if (wanChangS==schedule.getMatchState()) { //已完场
					if (wanChangS!=oldMatchState) { //之前的状态不是完场
						commonUtil.sendScheduleFinishJms(schedule); //发送完场的Jms
						jmsZqUtil.rankingUpdateJms(schedule.getScheduleID()); //更新联赛排名的Jms
					}
					//处理完场后比分发生变化的情况(球探网的比分错误,之后人工修改正确)
					if (wanChangS==oldMatchState && scoreModify) { //之前的状态是完场
						commonUtil.sendScoreModifyJms(schedule); //发送比分变化的Jms
						jmsZqUtil.rankingUpdateJms(schedule.getScheduleID()); //更新联赛排名的Jms
					}
				}
				if (wanChangS==oldMatchState&&wanChangS!=schedule.getMatchState()) {
					logger.info("比赛状态由完场变为其他状态,matchState:"+schedule.getMatchState()+",scheduleId:"+schedule.getScheduleID());
				}
				//发送赛事缓存更新的Jms
				jmsZqUtil.schedulesCacheUpdate(schedule.getScheduleID());
				jmsZqUtil.processingSchedulesCacheUpdate(); //进行中比赛缓存更新
				jmsZqUtil.schedulesByEventCacheUpdate(schedule.getEvent());
			}
		} catch(Exception e) {
			logger.error("足球-今日比分数据更新数据处理发生异常", e);
		}
	}
	
	/**
	 * 解析比赛时间
	 * @param matchTime(18:30)
	 * @param matchTime2(2013,6,1,19,24,25)
	 * @return
	 */
	private Map<String, String> getMatchTimeMap(Date oldMatchTime, String matchTime, String matchTime2) {
		Map<String, String> map = new HashMap<String, String>();
		if (oldMatchTime==null) {
			matchTime = "";
		} else {
			matchTime = DateUtil.format("yyyy-MM-dd", oldMatchTime)+" "+matchTime+":00";
		}
		
		String[] matchTime2s = StringUtils.split(matchTime2, ",");
		if (matchTime2s!=null&&matchTime2s.length==6) {
			matchTime2 = matchTime2s[0]+"-"+(Integer.valueOf(matchTime2s[1])+1)+"-"+matchTime2s[2]+" "
					+matchTime2s[3]+":"+matchTime2s[4]+":"+matchTime2s[5]; //传过来6代表7月份
		} else {
			matchTime2 = "";
		}
		map.put("matchTime", matchTime);
		map.put("matchTime2", matchTime2);
		return map;
	}
	
}
