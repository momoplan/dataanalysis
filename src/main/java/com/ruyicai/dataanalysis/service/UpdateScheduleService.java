package com.ruyicai.dataanalysis.service;

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
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.bd.SendJmsBdUtil;
import com.ruyicai.dataanalysis.util.jcz.SendJmsJczUtil;

@Service
public class UpdateScheduleService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateScheduleService.class);

	@Value("${saichensaiguo}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private SendJmsJczUtil sendJmsJczUtil;
	
	@Autowired
	private SendJmsBdUtil sendJmsBdUtil;
	
	public void getAllScheduleBySclass() {
		logger.info("开始获取足球所有联赛下所有赛事");
		long startmillis = System.currentTimeMillis();
		List<Sclass> sclasses = Sclass.findAllSclasses();
		logger.info("足球联赛size:{}", new Integer[] {sclasses.size()});
		for(Sclass sclass : sclasses) {
			processDateAndSclassID(null, String.valueOf(sclass.getSclassID()), false);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("获取足球所有联赛下所有赛事结束, 共用时 " + (endmillis - startmillis));
	}
	
	public void processCount(int count, int mode) {
		if(mode == 0) {
			for(int i = 0; i <= count; i ++) {
				processDateAndSclassID(DateUtil.getAfterDate(i), null, false);
			}
		}
		if(mode == 1) {
			for(int i = 0; i <= count; i ++) {
				processDateAndSclassID(DateUtil.getPreDate(i), null, false);
			}
		}
	}
	
	public void process() {
		logger.info("开始更新足球赛程赛果");
		long startmillis = System.currentTimeMillis();
		processDateAndSclassID(DateUtil.getPreDate(2), null, true);
		processDateAndSclassID(DateUtil.getPreDate(1), null, true);
		processDateAndSclassID(new Date(), null, true);
		processDateAndSclassID(DateUtil.getAfterDate(1), null, true);
		processDateAndSclassID(DateUtil.getAfterDate(2), null, true);
		processDateAndSclassID(DateUtil.getAfterDate(3), null, true);
		long endmillis = System.currentTimeMillis();
		logger.info("更新足球赛程赛果结束, 共用时 " + (endmillis - startmillis));
	}
	
	@SuppressWarnings("unchecked")
	public void processDateAndSclassID(Date date, String sclassID, boolean updateRanking) {
		logger.info("开始更新足球赛程赛果, date:{}, sclassID:{}", new String[] {DateUtil.format(date), sclassID});
		long startmillis = System.currentTimeMillis();
		try {
			String param = "";
			if(null != date) {
				param = "date=" + DateUtil.format("yyyy-MM-dd", date);
			}
			if(null != sclassID) {
				Sclass sclass = Sclass.findSclass(Integer.parseInt(sclassID));
				if(null == sclass) {
					return;
				}
				param = "sclassID=" + sclassID;
			}
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, param);
			if (StringUtil.isEmpty(data)) {
				logger.info("更新足球赛程赛果时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("match");
			for(Element match : matches) {
				doProcess(match, updateRanking);
			}
		} catch(Exception e) {
			logger.error("更新足球赛程赛果出错, date:" + DateUtil.format(date) + ",sclassID:" + sclassID, e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新足球赛程赛果结束, date:{}, sclassID:{}, 共用时 {}", new String[] {DateUtil.format(date), sclassID, String.valueOf((endmillis - startmillis))});
	}

	private void doProcess(Element match, boolean updateRanking) {
		try {
			String a = match.elementTextTrim("a"); 
			String c = match.elementTextTrim("c");
			String d = match.elementTextTrim("d");
			String f = match.elementTextTrim("f");
			String h = match.elementTextTrim("h");
			String i = match.elementTextTrim("i");
			String j = match.elementTextTrim("j");
			String k = match.elementTextTrim("k");
			String l = match.elementTextTrim("l");
			String m = match.elementTextTrim("m");
			String n = match.elementTextTrim("n");
			String o = match.elementTextTrim("o");
			String p = match.elementTextTrim("p");
			String q = match.elementTextTrim("q");
			String r = match.elementTextTrim("r");
			String s = match.elementTextTrim("s");
			String t = match.elementTextTrim("t");
			String u = match.elementTextTrim("u");
			String v = match.elementTextTrim("v");
			String w = match.elementTextTrim("w");
			String x = match.elementTextTrim("x");
			String y = match.elementTextTrim("y");
			String z = match.elementTextTrim("z");
			Integer neutrality = 0; //是否是中立场(1:是;0:否)
			if (StringUtils.equals(z, "True")) {
				neutrality = 1;
			}
			String[] homeInfos = h.split("\\,"); //主队信息
			String homeTeam = homeInfos[0]; //主队名称
			int homeTeamID = Integer.parseInt(homeInfos[3]); //主队编号
			String[] guestInfos = i.split("\\,"); //客队信息
			String guestTeam = guestInfos[0]; //客队名称
			int guestTeamID = Integer.parseInt(guestInfos[3]); //客队编号
			
			Schedule schedule = Schedule.findScheduleWOBuild(Integer.parseInt(a));
			boolean ismod = false;
			if(null == schedule) {
				schedule = new Schedule();
				schedule.setScheduleID(Integer.parseInt(a));
				schedule.setSclassID(Integer.parseInt(c.split("\\,")[3]));
				schedule.setMatchTime(DateUtil.parse("yyyy/MM/dd HH:mm:ss", d));
				schedule.setMatchState(Integer.parseInt(f));
				//String[] values = h.split("\\,");
				//schedule.setHomeTeam(values[0]);
				schedule.setHomeTeam(homeTeam);
				//schedule.setHomeTeamID(Integer.parseInt(values[3]));
				schedule.setHomeTeamID(homeTeamID);
				//values = i.split("\\,");
				//schedule.setGuestTeam(values[0]);
				schedule.setGuestTeam(guestTeam);
				//schedule.setGuestTeamID(Integer.parseInt(values[3]));
				schedule.setGuestTeamID(guestTeamID);
				schedule.setHomeScore(NumberUtil.parseInt(j, 0));
				schedule.setGuestScore(NumberUtil.parseInt(k, 0));
				schedule.setHomeHalfScore(NumberUtil.parseInt(l, 0));
				schedule.setGuestHalfScore(NumberUtil.parseInt(m, 0));
				schedule.setHome_Red(NumberUtil.parseInt(n, 0));
				schedule.setGuest_Red(NumberUtil.parseInt(o, 0));
				schedule.setHome_Order(p);
				schedule.setGuest_Order(q);
				schedule.setExplain(r);
				schedule.setRound(NumberUtil.parseInt(s, 0));
				schedule.setLocation(t);
				schedule.setWeatherIcon(NumberUtil.parseInt(u, 0));
				schedule.setWeather(v);
				schedule.setTemperature(w);
				schedule.setMatchSeason(x);
				schedule.setGrouping(y);
				schedule.setNeutrality(neutrality);
				schedule.persist();
				updateRanking(schedule.getScheduleID(), updateRanking);
			} else {
				int matchstate = schedule.getMatchState();
				if (StringUtils.isNotBlank(d)) {
					String pattern = "yyyy/MM/dd HH:mm:ss";
					Date dDate = DateUtil.parse(pattern, d);
					String dDateStr = DateUtil.format(pattern, dDate);
					String matchTimeOldStr = DateUtil.format(pattern, schedule.getMatchTime());
					if (!StringUtils.equals(dDateStr, matchTimeOldStr)) {
						ismod = true;
						schedule.setMatchTime(dDate);
					}
				}
				if (!StringUtils.equals(schedule.getHomeTeam(), homeTeam)) {
					ismod = true;
					schedule.setHomeTeam(homeTeam);
				}
				if (schedule.getHomeTeamID()!=homeTeamID) {
					ismod = true;
					schedule.setHomeTeamID(homeTeamID);
				}
				if (StringUtils.equals(schedule.getGuestTeam(), guestTeam)) {
					ismod = true;
					schedule.setGuestTeam(guestTeam);
				}
				if (schedule.getGuestTeamID()!=guestTeamID) {
					ismod = true;
					schedule.setGuestTeamID(guestTeamID);
				}
				if(Integer.parseInt(f) != schedule.getMatchState()) {
					ismod = true;
					schedule.setMatchState(Integer.parseInt(f));
				}
				if(NumberUtil.parseInt(j, 0) != schedule.getHomeScore()) {
					ismod = true;
					schedule.setHomeScore(NumberUtil.parseInt(j, 0));
				}
				if(NumberUtil.parseInt(k, 0) != schedule.getGuestScore()) {
					ismod = true;
					schedule.setGuestScore(NumberUtil.parseInt(k, 0));
				}
				if(NumberUtil.parseInt(l, 0) != schedule.getHomeHalfScore()) {
					ismod = true;
					schedule.setHomeHalfScore(NumberUtil.parseInt(l, 0));
				}
				if(NumberUtil.parseInt(m, 0) != schedule.getGuestHalfScore()) {
					ismod = true;
					schedule.setGuestHalfScore(NumberUtil.parseInt(m, 0));
				}
				if(NumberUtil.parseInt(n, 0) != schedule.getHome_Red()) {
					ismod = true;
					schedule.setHome_Red(NumberUtil.parseInt(n, 0));
				}
				if(NumberUtil.parseInt(o, 0) != schedule.getGuest_Red()) {
					ismod = true;
					schedule.setGuest_Red(NumberUtil.parseInt(o, 0));
				}
				if(null != p && !p.equals(schedule.getHome_Order())) {
					ismod = true;
					schedule.setHome_Order(p);
				}
				if(null != q && !q.equals(schedule.getGuest_Order())) {
					ismod = true;
					schedule.setGuest_Order(q);
				}
				if(NumberUtil.parseInt(u, 0) != schedule.getWeatherIcon()) {
					ismod = true;
					schedule.setWeatherIcon(NumberUtil.parseInt(u, 0));
				}
				if(v != null && !v.equals(schedule.getWeather())) {
					ismod = true;
					schedule.setWeather(v);
				}
				if(w != null && !w.equals(schedule.getTemperature())) {
					ismod = true;
					schedule.setTemperature(w);
				}
				if (neutrality!=schedule.getNeutrality()) {
					ismod = true;
					schedule.setNeutrality(neutrality);
				}
				if(ismod) {
					schedule.merge();
					//已完场
					if(MatchState.WANCHANG.value!=matchstate && MatchState.WANCHANG.value==schedule.getMatchState()) {
						//发送完场的Jms
						String event = schedule.getEvent(); //竞彩足球
						if (StringUtils.isNotBlank(event)) {
							sendJmsJczUtil.sendScheduleFinishJms(event);
						}
						String bdEvent = schedule.getBdEvent(); //北单
						if (StringUtils.isNotBlank(bdEvent)) {
							sendJmsBdUtil.sendScheduleFinishJms(bdEvent);
						}
						//更新排名
						updateRanking(schedule.getScheduleID(), updateRanking);
					}
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void updateRanking(int scheduleID, boolean updateRanking) {
		if(updateRanking) {
			try {
				analysisService.getRanking(scheduleID, false);
			} catch(Exception e) {
				logger.error("更新足球联赛排名出错, scheduleID:" + scheduleID, e);
			}
		}
	}
	
	/**
	 * 根据日期更新赛事
	 * @param dateString
	 */
	public void updateScheduleByDate(String dateString) {
		Date date = DateUtil.parse("yyyy-MM-dd", dateString);
		processDateAndSclassID(date, null, false);
	}
	
	/**
	 * 查询之后30天的赛事
	 */
	public void processMore() {
		logger.info("获取之后30天的足球赛事开始");
		processCount(30, 0);
		logger.info("获取之后30天的足球赛事结束");
	}
	
}
