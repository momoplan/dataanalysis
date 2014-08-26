package com.ruyicai.dataanalysis.timer.lq;

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

import com.ruyicai.dataanalysis.consts.lq.MatchStateJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.listener.lq.SchedulesJclCacheUpdateListener;
import com.ruyicai.dataanalysis.service.lq.AnalysisJclService;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.lq.SendJmsJclUtil;

/**
 * 竞彩篮球-赛程赛果更新
 * @author Administrator
 *
 */
@Service
public class ScheduleJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(ScheduleJclUpdateService.class);
	
	private Calendar calendar = Calendar.getInstance();
	
	@Value("${matchResultJclUrl}")
	private String matchResultJclUrl;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private SendJmsJclUtil sendJmsJclUtil;
	
	@Autowired
	private SchedulesJclCacheUpdateListener schedulesJclCacheUpdateListener;
	
	public void process() {
		logger.info("竞彩篮球-赛程赛果更新开始");
		long startmillis = System.currentTimeMillis();
		processDateAndSclassID(DateUtil.getPreDate(2), true);
		processDateAndSclassID(DateUtil.getPreDate(1), true);
		processDateAndSclassID(new Date(), true);
		processDateAndSclassID(DateUtil.getAfterDate(1), true);
		processDateAndSclassID(DateUtil.getAfterDate(2), true);
		processDateAndSclassID(DateUtil.getAfterDate(3), true);
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-赛程赛果更新结束,共用时 " + (endmillis - startmillis));
	}
	
	@SuppressWarnings("unchecked")
	public void processDateAndSclassID(Date date, boolean updateRanking) {
		logger.info("竞彩篮球-赛程赛果更新开始, date:{}", new String[] {DateUtil.format(date)});
		long startmillis = System.currentTimeMillis();
		try {
			String param = "";
			if(null != date) {
				param = "time=" + DateUtil.format("yyyy-MM-dd", date);
			}
//			String data = httpUtil.getResponse(matchResultJclUrl, HttpUtil.GET, HttpUtil.GBK, param);
			String data = httpUtil.getResponse(matchResultJclUrl, HttpUtil.GET, HttpUtil.UTF8, param);
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-赛程赛果更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
//			List<Element> matches = doc.getRootElement().element("m").elements("h");
			List<Element> matches = doc.getRootElement().elements("h");
			for(Element match : matches) {
				doProcess(match, date, updateRanking);
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-赛程赛果更新异常, date:" + DateUtil.format(date), e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-赛程赛果更新结束, date:{}, 共用时 {}", new String[] {DateUtil.format(date), String.valueOf((endmillis - startmillis))});
	}
	
	/**
	 * 解析数据
	 * @param match
	 * @param date
	 */
	private void doProcess(Element match, Date date, boolean updateRanking) {
		try {
			String info = match.getText();
			String[] infos = info.split("\\^");
			String scheduleId = infos[0]; //赛事ID
			String sclassName = infos[3]; //联赛名(如 NBA,WNBA)
			String sclassNameJs = sclassName.split(",")[0]; //联赛名(简体简称)
			String sclassType = infos[4]; //分几节进行(2:上下半场;4:分4小节)
			String matchTime = infos[6]; //开赛时间(2014/8/20 18:15:00)
			matchTime = matchTime.replaceAll("<br>", " "); //去掉换行符
			matchTime = convertMatchTime(matchTime);
			String matchState = infos[7]; //状态
			String remainTime = infos[8]; //小节剩余时间
			String homeTeamId = infos[9]; //主队ID
			String homeTeam = infos[10].indexOf(",")>-1?infos[10].split(",")[0]:""; //主队名
			String guestTeamId = infos[11]; //客队ID
			String guestTeam = infos[12].indexOf(",")>-1?infos[12].split(",")[0]:""; //客队名
			String homeScore = infos[15]; //主队得分
			String guestScore = infos[16]; //客队得分
			String homeOne = infos[17]; //主队一节得分(上半场)
			String guestOne = infos[18]; //客队一节得分（上半场）
			String homeTwo = infos[19]; //主队二节得分
			String guestTwo = infos[20]; //客队二节得分
			String homeThree = infos[21]; //主队三节得分(下半场）
			String guestThree = infos[22]; //客队三节得分(下半场）
			String homeFour = infos[23]; //主队四节得分
			String guestFour = infos[24]; //客队四节得分
			String addTime = infos[25]; //加时数
			String homeAddTime1 = infos[26]; //主队1'ot得分
			String guestAddTime1 = infos[27]; //客队1'ot得分
			String homeAddTime2 = infos[28]; //主队2'ot得分
			String guestAddTime2 = infos[29]; //客队2'ot得分
			String homeAddTime3 = infos[30]; //主队3'ot得分
			String guestAddTime3 = infos[31]; //客队3'ot得分
			//==========新接口中主胜赔率与客胜赔率取消了==============
			String homeWinLv = ""; //主胜赔率
			String guestWinLv = ""; //客胜赔率
			String matchSeason = infos[36]; //赛季(如:12-13赛季)
			if (matchSeason!=null&&matchSeason.endsWith("赛季")) {
				matchSeason = matchSeason.substring(0, matchSeason.length()-2).trim();
			}
			String matchType = infos[37]; //类型(如季前赛,常规赛,季后赛)
			String sclassId = infos[1]; //联赛ID
			
			ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJclNotBuild(Integer.parseInt(scheduleId));
			if (scheduleJcl==null) {
				scheduleJcl = new ScheduleJcl();
				scheduleJcl.setScheduleId(Integer.parseInt(scheduleId));
				scheduleJcl.setSclassId(Integer.parseInt(sclassId));
				scheduleJcl.setSclassNameJs(sclassNameJs);
				scheduleJcl.setSclassType(sclassType);
				scheduleJcl.setMatchTime(DateUtil.parse("yyyy年MM月dd日 HH:mm:ss", matchTime));
				scheduleJcl.setMatchState(matchState);
				scheduleJcl.setRemainTime(remainTime);
				scheduleJcl.setHomeTeamId(homeTeamId);
				scheduleJcl.setHomeTeam(homeTeam);
				scheduleJcl.setGuestTeamId(guestTeamId);
				scheduleJcl.setGuestTeam(guestTeam);
				scheduleJcl.setHomeScore(NumberUtil.parseString(homeScore, "0"));
				scheduleJcl.setGuestScore(NumberUtil.parseString(guestScore, "0"));
				scheduleJcl.setHomeOne(NumberUtil.parseString(homeOne, "0"));
				scheduleJcl.setGuestOne(NumberUtil.parseString(guestOne, "0"));
				scheduleJcl.setHomeTwo(NumberUtil.parseString(homeTwo, "0"));
				scheduleJcl.setGuestTwo(NumberUtil.parseString(guestTwo, "0"));
				scheduleJcl.setHomeThree(NumberUtil.parseString(homeThree, "0"));
				scheduleJcl.setGuestThree(NumberUtil.parseString(guestThree, "0"));
				scheduleJcl.setHomeFour(NumberUtil.parseString(homeFour, "0"));
				scheduleJcl.setGuestFour(NumberUtil.parseString(guestFour, "0"));
				scheduleJcl.setAddTime(addTime);
				scheduleJcl.setHomeAddTime1(NumberUtil.parseString(homeAddTime1, "0"));
				scheduleJcl.setGuestAddTime1(NumberUtil.parseString(guestAddTime1, "0"));
				scheduleJcl.setHomeAddTime2(NumberUtil.parseString(homeAddTime2, "0"));
				scheduleJcl.setGuestAddTime2(NumberUtil.parseString(guestAddTime2, "0"));
				scheduleJcl.setHomeAddTime3(NumberUtil.parseString(homeAddTime3, "0"));
				scheduleJcl.setGuestAddTime3(NumberUtil.parseString(guestAddTime3, "0"));
				scheduleJcl.setHomeWinLv(homeWinLv);
				scheduleJcl.setGuestWinLv(guestWinLv);
				scheduleJcl.setMatchSeason(matchSeason);
				scheduleJcl.setMatchType(matchType);
				scheduleJcl.persist();
				//更新联赛排名
				updateRanking(scheduleJcl.getScheduleId(), updateRanking);
			} else {
				boolean isModify = false; //是否变化
				boolean scoreModify = false;
				
				Integer sclassId_old = scheduleJcl.getSclassId();
				if (!StringUtil.isEmpty(sclassId) && (sclassId_old==null || sclassId_old!=Integer.parseInt(sclassId))) {
					isModify = true;
					scheduleJcl.setSclassId(Integer.parseInt(sclassId));
				}
				
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
				
				Date matchTime_old = scheduleJcl.getMatchTime();
				if (!StringUtil.isEmpty(matchTime) && (matchTime_old==null 
						|| !DateUtil.format("yyyy年MM月dd日 HH:mm:ss", matchTime_old).equals(matchTime))) {
					isModify = true;
					scheduleJcl.setMatchTime(DateUtil.parse("yyyy年MM月dd日 HH:mm:ss", matchTime));
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
				
				String homeWinLv_old = scheduleJcl.getHomeWinLv();
				if (!StringUtil.isEmpty(homeWinLv) && (StringUtil.isEmpty(homeWinLv_old)||!homeWinLv.equals(homeWinLv_old))) {
					isModify = true;
					scheduleJcl.setHomeWinLv(homeWinLv);
				}
				
				String guestWinLv_old = scheduleJcl.getGuestWinLv();
				if (!StringUtil.isEmpty(guestWinLv) && (StringUtil.isEmpty(guestWinLv_old)||!guestWinLv.equals(guestWinLv_old))) {
					isModify = true;
					scheduleJcl.setGuestWinLv(guestWinLv);
				}
				
				String matchSeason_old = scheduleJcl.getMatchSeason();
				if (!StringUtil.isEmpty(matchSeason) && (StringUtil.isEmpty(matchSeason_old)||!matchSeason.equals(matchSeason_old))) {
					isModify = true;
					scheduleJcl.setMatchSeason(matchSeason);
				}
				
				String matchType_old = scheduleJcl.getMatchType();
				if (!StringUtil.isEmpty(matchType) && (StringUtil.isEmpty(matchType_old)||!matchType.equals(matchType_old))) {
					isModify = true;
					scheduleJcl.setMatchType(matchType);
				}
				
				if (isModify) {
					scheduleJcl.merge();
					if (StringUtils.equals(scheduleJcl.getMatchState(), MatchStateJcl.wanChang.value())) { //已完场
						if (!StringUtils.equals(matchState_old, MatchStateJcl.wanChang.value())) { //之前的状态不是完场
							String event = scheduleJcl.getEvent();
							if (StringUtils.isNotBlank(event)) {
								sendJmsJclUtil.sendScheduleFinishJms(event, scheduleJcl); //发送完场的Jms
							}
							updateRanking(scheduleJcl.getScheduleId(), updateRanking); //更新排名
						}
						//处理完场后比分发生变化的情况(球探网的比分错误,之后人工修改正确)
						if (StringUtils.equals(matchState_old, MatchStateJcl.wanChang.value()) && scoreModify) {
							String event = scheduleJcl.getEvent();
							if (StringUtils.isNotBlank(event)) { 
								sendJmsJclUtil.sendScoreModifyJms(event, scheduleJcl); //发送比分变化的Jms
							}
							updateRanking(scheduleJcl.getScheduleId(), updateRanking); //更新排名
						}
					}
					//发送赛事缓存更新的Jms
					sendJmsJclUtil.sendSchedulesCacheUpdateJms(scheduleJcl.getScheduleId());
				}
			}
		} catch(Exception e) {
			logger.error("解析竞彩篮球-赛程赛果异常", e);
		}
	}
	
	/**
	 * 更新联赛排名
	 * @param scheduleID
	 * @param updateRanking
	 */
	private void updateRanking(int scheduleID, boolean updateRanking) {
		if(updateRanking) {
			try {
				analysisJclService.getRanking(scheduleID, false);
			} catch(Exception e) {
				logger.error("竞彩篮球-更新联赛排名出错, scheduleID:" + scheduleID, e);
			}
		}
	}
	
	/**
	 * 根据天数获取赛事
	 */
	public void updateScheduleByDays(int count, int mode) {
		if(mode == 0) {
			for(int i = 0; i <= count; i ++) {
				processDateAndSclassID(DateUtil.getAfterDate(i), false);
			}
		}
		if(mode == 1) {
			for(int i = 0; i <= count; i ++) {
				processDateAndSclassID(DateUtil.getPreDate(i), false);
			}
		}
	}
	
	/**
	 * 查询之后30天的赛事
	 */
	public void processMore() {
		logger.info("获取之后30天的篮球赛事开始");
		updateScheduleByDays(30, 0);
		logger.info("获取之后30天的篮球赛事结束");
	}
	
	public void processWeiKai() {
		try {
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, -30);
			Date startDate = calendar.getTime();
			Date endDate = new Date();
			processWeiKaiByMatchtime(startDate, endDate);
		} catch (Exception e) {
			logger.error("篮球processWeiKai发生异常", e);
		}
	}
	
	public void processWeiKaiByMatchtime(Date startDate, Date endDate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			logger.info("篮球根据matchtime处理未开赛事 start,startDate="+sdf.format(startDate)+",endDate="+sdf.format(endDate));
			List<ScheduleJcl> list = ScheduleJcl.findWeiKai(startDate, endDate);
			if (list!=null && list.size()>0) {
				Set<String> set = new HashSet<String>();
				for (ScheduleJcl scheduleJcl : list) {
					String event = scheduleJcl.getEvent();
					Date matchTime = scheduleJcl.getMatchTime(); //比赛时间
					if (StringUtils.isBlank(event)) {
						scheduleJcl.remove();
						logger.info("篮球赛事删除,id="+scheduleJcl.getScheduleId());
						if (matchTime!=null) {
							set.add(sdf.format(matchTime));
						}
					}
				}
				if (set!=null && set.size()>0) {
					for (String dateString : set) {
						Date date = DateUtil.parse("yyyy-MM-dd", dateString);
						schedulesJclCacheUpdateListener.updateCacheByDate(date); //更新赛事缓存
					}
				}
			}
		} catch (Exception e) {
			logger.error("篮球根据matchtime处理未开赛事发生异常", e);
		}
	}
	
	/**
	 * 将matchTime转换成标准格式
	 * @param matchTime 2014/2/11 8:15:00
	 * @return
	 */
	public String convertMatchTime(String matchTime){
		Date date = DateUtil.parse("yyyy/MM/dd HH:mm:ss", matchTime);
		return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
	}
	
}
