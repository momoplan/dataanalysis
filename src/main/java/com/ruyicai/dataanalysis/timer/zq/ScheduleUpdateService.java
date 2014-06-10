package com.ruyicai.dataanalysis.timer.zq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.ruyicai.dataanalysis.listener.zq.SchedulesCacheUpdateListener;
import com.ruyicai.dataanalysis.service.AnalysisService;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.zq.JmsZqUtil;

@Service
public class ScheduleUpdateService {
	
	private Logger logger = LoggerFactory.getLogger(ScheduleUpdateService.class);
	
	private Calendar calendar = Calendar.getInstance();

	@Value("${saichensaiguo}")
	private String url;
	
	@Value("${scheduleById}")
	private String scheduleByIdUrl;
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private JmsZqUtil jmsZqUtil;
	
	@Autowired
	private SchedulesCacheUpdateListener schedulesCacheUpdateListener;
	
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
	
	public void processCount(int count, int mode, boolean includeZero) {
		if(mode == 0) {
			for(int i = 0; i <= count; i ++) {
				if (i==0 && !includeZero) {
					continue;
				}
				processDateAndSclassID(DateUtil.getAfterDate(i), null, false);
			}
		}
		if(mode == 1) {
			for(int i = 0; i <= count; i ++) {
				if (i==0 && !includeZero) {
					continue;
				}
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
		logger.info("更新足球赛程赛果结束, date:{},sclassID:{},共用时 {}", new String[] {DateUtil.format(date), sclassID, String.valueOf((endmillis - startmillis))});
	}

	private void doProcess(Element match, boolean updateRanking) {
		try {
			String a = match.elementTextTrim("a"); //比赛ID
			String c = match.elementTextTrim("c"); //联赛国语名,联赛繁体名,联赛英文名,联赛ID
			String d = match.elementTextTrim("d"); //比赛时间
			String f = match.elementTextTrim("f"); //比赛状态
			String h = match.elementTextTrim("h"); //主队国语名, 主队繁体名, 主队英文名, 主队ID
			String i = match.elementTextTrim("i"); //客队国语名, 客队繁体名, 客队英文名, 客队ID
			String j = match.elementTextTrim("j"); //主队比分
			String k = match.elementTextTrim("k"); //客队比分
			String l = match.elementTextTrim("l"); //主队半场比分
			String m = match.elementTextTrim("m"); //客队半场比分
			String n = match.elementTextTrim("n"); //主队红牌
			String o = match.elementTextTrim("o"); //客队红牌
			String p = match.elementTextTrim("p"); //主队排名
			String q = match.elementTextTrim("q"); //客队排名
			String r = match.elementTextTrim("r"); //<![CDATA[赛事说明]]>
			String s = match.elementTextTrim("s"); //轮次/分组名，例如 4/8强/准决赛
			String t = match.elementTextTrim("t"); //比赛地点
			String u = match.elementTextTrim("u"); //天气图标
			String v = match.elementTextTrim("v"); //天气
			String w = match.elementTextTrim("w"); //温度
			String x = match.elementTextTrim("x"); //赛季
			String y = match.elementTextTrim("y"); //小组分组
			String z = match.elementTextTrim("z"); //是否中立场
			
			Integer neutrality = StringUtils.equals(z, "True") ? 1 : 0; //是否是中立场(1:是;0:否)
			String[] homeInfos = h.split("\\,"); //主队信息
			String homeTeam = homeInfos[0]; //主队名称
			int homeTeamID = Integer.parseInt(homeInfos[3]); //主队编号
			String[] guestInfos = i.split("\\,"); //客队信息
			String guestTeam = guestInfos[0]; //客队名称
			int guestTeamID = Integer.parseInt(guestInfos[3]); //客队编号
			Integer scheduleId = Integer.parseInt(a); //比赛id
			String[] sclassInfos = c.split("\\,"); //联赛信息
			Integer sclassId = Integer.parseInt(sclassInfos[3]); //联赛ID
			Date matchTime = DateUtil.parse("yyyy/MM/dd HH:mm:ss", d); //比赛时间
			Integer matchState = Integer.parseInt(f); //比赛状态
			Integer homeScore = NumberUtil.parseInt(j, 0); //主队比分
			Integer guestScore = NumberUtil.parseInt(k, 0); //客队比分
			Integer homeHalfScore = NumberUtil.parseInt(l, 0); //主队半场比分
			Integer guestHalsScore = NumberUtil.parseInt(m, 0); //客队半场比分
			Integer homeRed = NumberUtil.parseInt(n, 0); //主队红牌
			Integer guestRed = NumberUtil.parseInt(o, 0); //客队红牌
			Integer round = NumberUtil.parseInt(s, 0); //轮次
			Integer weatherIcon = NumberUtil.parseInt(u, 0); //天气图标
			
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			if(schedule == null) {
				schedule = new Schedule();
				schedule.setScheduleID(scheduleId);
				schedule.setSclassID(sclassId);
				schedule.setMatchTime(matchTime);
				schedule.setMatchState(matchState);
				schedule.setHomeTeam(homeTeam);
				schedule.setHomeTeamID(homeTeamID);
				schedule.setGuestTeam(guestTeam);
				schedule.setGuestTeamID(guestTeamID);
				schedule.setHomeScore(homeScore);
				schedule.setGuestScore(guestScore);
				schedule.setHomeHalfScore(homeHalfScore);
				schedule.setGuestHalfScore(guestHalsScore);
				schedule.setHome_Red(homeRed);
				schedule.setGuest_Red(guestRed);
				schedule.setHome_Order(p);
				schedule.setGuest_Order(q);
				schedule.setExplain(r);
				schedule.setRound(round);
				schedule.setLocation(t);
				schedule.setWeatherIcon(weatherIcon);
				schedule.setWeather(v);
				schedule.setTemperature(w);
				schedule.setMatchSeason(x);
				schedule.setGrouping(y);
				schedule.setNeutrality(neutrality);
				schedule.persist();
				updateRanking(schedule.getScheduleID(), updateRanking);
			} else {
				boolean ismod = false;
				boolean scoreModify = false;
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
				if (!StringUtils.equals(schedule.getGuestTeam(), guestTeam)) {
					ismod = true;
					schedule.setGuestTeam(guestTeam);
				}
				if (schedule.getGuestTeamID()!=guestTeamID) {
					ismod = true;
					schedule.setGuestTeamID(guestTeamID);
				}
				Integer oldMatchState = schedule.getMatchState();
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
				if(guestHalsScore != schedule.getGuestHalfScore()) {
					ismod = true;
					scoreModify = true;
					schedule.setGuestHalfScore(guestHalsScore);
				}
				if(homeRed != schedule.getHome_Red()) {
					ismod = true;
					schedule.setHome_Red(homeRed);
				}
				if(guestRed != schedule.getGuest_Red()) {
					ismod = true;
					schedule.setGuest_Red(guestRed);
				}
				if(null != p && !p.equals(schedule.getHome_Order())) {
					ismod = true;
					schedule.setHome_Order(p);
				}
				if(null != q && !q.equals(schedule.getGuest_Order())) {
					ismod = true;
					schedule.setGuest_Order(q);
				}
				if(weatherIcon != schedule.getWeatherIcon()) {
					ismod = true;
					schedule.setWeatherIcon(weatherIcon);
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
					if (MatchState.WANCHANG.value==schedule.getMatchState()) { //已完场
						if (MatchState.WANCHANG.value!=oldMatchState) { //之前的状态不是完场
							commonUtil.sendScheduleFinishJms(schedule); //发送完场的Jms
							updateRanking(schedule.getScheduleID(), updateRanking); //更新排名
						}
						//处理完场后比分发生变化的情况(球探网的比分错误,之后人工修改正确)
						if (MatchState.WANCHANG.value==oldMatchState && scoreModify) { //之前的状态是完场
							commonUtil.sendScoreModifyJms(schedule); //发送比分变化的Jms
							updateRanking(schedule.getScheduleID(), updateRanking); //更新排名
						}
					}
					//发送赛事缓存更新的Jms
					jmsZqUtil.schedulesCacheUpdate(schedule.getScheduleID());
					jmsZqUtil.schedulesByEventCacheUpdate(schedule.getEvent());
				}
			}
		} catch(Exception e) {
			logger.error("足球处理赛程赛果发生异常", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> updateScheduleById(String scheduleId, Set<String> set) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			logger.info("根据id更新赛事,id="+scheduleId);
			if (StringUtils.isBlank(scheduleId)) {
				return set;
			}
			String param = "id="+scheduleId;
			String data = httpUtil.getResponse(scheduleByIdUrl, HttpUtil.GET, HttpUtil.UTF8, param);
			if (StringUtils.isBlank(data)) {
				logger.info("根据id更新赛事,获取数据为空,id="+scheduleId);
				return set;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("match");
			
			Schedule schedule = Schedule.findById(Integer.parseInt(scheduleId), false);
			Date matchTime = schedule.getMatchTime(); //比赛时间
			if (matches==null || matches.size()==0) { //删除赛事
				boolean zqEventEmpty = CommonUtil.isZqEventEmpty(schedule);
				if (schedule!=null && zqEventEmpty) {
					schedule.remove();
					logger.info("足球赛事删除,id="+scheduleId);
					if (matchTime!=null) {
						set.add(sdf.format(matchTime));
					}
					return set;
				}
			} else if (matches.size()==1) { //更新赛事
				Element match = matches.get(0);
				doProcess(match, true);
				if (matchTime!=null) { //之前的比赛时间
					set.add(sdf.format(matchTime));
				}
				String d = match.elementTextTrim("d"); //现在的比赛时间
				Date time = DateUtil.parse("yyyy/MM/dd HH:mm:ss", d);
				if (time!=null) {
					set.add(sdf.format(time));
				}
				return set;
			}
		} catch (Exception e) {
			logger.error("根据id更新赛事发生异常,id="+scheduleId, e);
		}
		return set;
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
	 * 查询之后30天的赛事
	 */
	public void processMore() {
		logger.info("获取之后30天的足球赛事开始");
		processCount(30, 0, true);
		logger.info("获取之后30天的足球赛事结束");
		/*logger.info("获取之前30天的足球赛事开始");
		processCount(30, 1, false);
		logger.info("获取之前30天的足球赛事结束");*/
	}
	
	public void processWeiKai() {
		try {
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -30);
			Date startDate = calendar.getTime();
			Date endDate = new Date();
			processWeiKaiByMatchtime(startDate, endDate);
		} catch (Exception e) {
			logger.error("足球processWeiKai发生异常", e);
		}
	}
	
	public void processWeiKaiByMatchtime(Date startDate, Date endDate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			logger.info("足球根据matchtime更新未开赛事 start,startDate="+sdf.format(startDate)+",endDate="+sdf.format(endDate));
			List<Schedule> list = Schedule.findWeiKai(startDate, endDate);
			if (list!=null && list.size()>0) {
				Set<String> set = new HashSet<String>();
				for (Schedule schedule : list) {
					updateScheduleById(String.valueOf(schedule.getScheduleID()), set);
				}
				if (set!=null && set.size()>0) {
					for (String dateString : set) {
						Date date = DateUtil.parse("yyyy-MM-dd", dateString);
						schedulesCacheUpdateListener.updateCacheByDate(date, System.currentTimeMillis(), 0); //更新赛事缓存
					}
				}
			}
		} catch (Exception e) {
			logger.error("足球根据matchtime更新未开赛事发生异常", e);
		}
	}
	
	/*public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Set<Date> set=new HashSet<Date>();
		set.add(new Date());
		set.add(new Date());
		set.add(new Date());
		//System.out.println(new Date().getTime());
		Date parse = DateUtil.parse("yyyy-MM-dd", "2014-01-02");
		//System.out.println(parse.getTime());
		set.add(parse);
		for (Date str : set) {
			System.out.println(sdf.format(str));
		}
	}*/
	
}
