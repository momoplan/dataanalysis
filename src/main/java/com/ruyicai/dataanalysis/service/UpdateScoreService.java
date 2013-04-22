package com.ruyicai.dataanalysis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class UpdateScoreService {

	private Logger logger = LoggerFactory.getLogger(UpdateScoreService.class);
	
	@Value("${todaybifeng}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@Autowired
	private AnalysisService analysisService;

	public void process() {
		logger.info("开始更新当天比分数据");
		long startmillis = System.currentTimeMillis();
		String data = httpUtil.downfile(url, HttpUtil.GBK);
		if (StringUtil.isEmpty(data)) {
			logger.info("更新当天比分数据时获取数据为空");
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
		logger.info("更新当天比分数据结束,共用时 {}", new Long[] {endmillis - startmillis});
	}

	private void doProcess(String data) {
		try {
			data = data.replaceFirst("A\\[\\d+\\]=", "");
			data = data.replaceAll("\\.split\\('\\^'\\)", "");
			data = data.replaceAll("\"", "");
			String[] values = data.split("\\^");
			String scheduleID = values[0];
			String matchState = values[13];
			String homeScore = values[14];
			String guestScore = values[15];
			String homeHalfScore = values[16];
			String guestHalfScore = values[17];
			String home_Red = values[18];
			String guest_Red = values[19];
			String home_yellow = values[20];
			String guest_Yellow = values[21];
			String homeOrder = values[22];
			String guestOrder = values[23];
			Schedule schedule = Schedule.findScheduleWOBuild(Integer.parseInt(scheduleID));
			if(null == schedule) {
				return;
			}
			int oldMatchState = schedule.getMatchState();
			boolean ismod = false;
			if(Integer.parseInt(matchState) != schedule.getMatchState()) {
				ismod = true;
				schedule.setMatchState(Integer.parseInt(matchState));
			}
			if(NumberUtil.parseInt(homeScore, 0) != schedule.getHomeScore()) {
				ismod = true;
				schedule.setHomeScore(NumberUtil.parseInt(homeScore, 0));
			}
			if(NumberUtil.parseInt(guestScore, 0) != schedule.getGuestScore()) {
				ismod = true;
				schedule.setGuestScore(NumberUtil.parseInt(guestScore, 0));
			}
			if(NumberUtil.parseInt(homeHalfScore, 0) != schedule.getHomeHalfScore()) {
				ismod = true;
				schedule.setHomeHalfScore(NumberUtil.parseInt(homeHalfScore, 0));
			}
			if(NumberUtil.parseInt(guestHalfScore, 0) != schedule.getGuestHalfScore()) {
				ismod = true;
				schedule.setGuestHalfScore(NumberUtil.parseInt(guestHalfScore, 0));
			}
			if(NumberUtil.parseInt(home_Red, 0) != schedule.getHome_Red()) {
				ismod = true;
				schedule.setHome_Red(NumberUtil.parseInt(home_Red, 0));
			}
			if(NumberUtil.parseInt(guest_Red, 0) != schedule.getGuest_Red()) {
				ismod = true;
				schedule.setGuest_Red(NumberUtil.parseInt(guest_Red, 0));
			}
			if(NumberUtil.parseInt(home_yellow, 0) != schedule.getHome_Yellow()) {
				ismod = true;
				schedule.setHome_Yellow(NumberUtil.parseInt(home_yellow, 0));
			}
			if(NumberUtil.parseInt(guest_Yellow, 0) != schedule.getGuest_Yellow()) {
				ismod = true;
				schedule.setGuest_Yellow(NumberUtil.parseInt(guest_Yellow, 0));
			}
			if(!StringUtil.isEmpty(homeOrder) && !homeOrder.equals(schedule.getHome_Order())) {
				ismod = true;
				schedule.setHome_Order(homeOrder);
			}
			if(!StringUtil.isEmpty(guestOrder) && !guestOrder.equals(schedule.getGuest_Order())) {
				ismod = true;
				schedule.setGuest_Order(guestOrder);
			}
			if(ismod) {
				schedule.merge();
				if(MatchState.WANCHANG.value != oldMatchState && MatchState.WANCHANG.value == schedule.getMatchState()) {
					analysisService.sendUpdateRankingJMS(schedule.getScheduleID());
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
