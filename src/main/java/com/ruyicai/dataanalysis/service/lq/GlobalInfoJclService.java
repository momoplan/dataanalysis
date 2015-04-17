package com.ruyicai.dataanalysis.service.lq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.consts.lq.MatchStateJcl;
import com.ruyicai.dataanalysis.domain.lq.CompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.EuropeCompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.SclassJcl;
import com.ruyicai.dataanalysis.domain.lq.StandardJcl;
import com.ruyicai.dataanalysis.domain.lq.TotalScoreJcl;
import com.ruyicai.dataanalysis.dto.BetNumDto;
import com.ruyicai.dataanalysis.dto.BetRatioDto;
import com.ruyicai.dataanalysis.dto.lq.AnalysisJclDto;
import com.ruyicai.dataanalysis.dto.lq.ClasliAnalysisJclDto;
import com.ruyicai.dataanalysis.dto.lq.InfoJclDTO;
import com.ruyicai.dataanalysis.dto.lq.RankingJclDTO;
import com.ruyicai.dataanalysis.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.service.CommonService;
import com.ruyicai.dataanalysis.service.back.AnalyzeService;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;
import com.ruyicai.dataanalysis.util.lq.CalcJclUtil;

@Service
public class GlobalInfoJclService {

	private Logger logger = LoggerFactory.getLogger(GlobalInfoJclService.class);
	
	private Calendar calendar = Calendar.getInstance();
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private LetgoalJclService letgoalJclService;
	
	@Autowired
	private AnalyzeService analyzeService;
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * 数据分析
	 * @param event
	 * @return
	 */
	public InfoJclDTO getInfo(String event) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, true);
		if(null == scheduleJcl) {
			return null;
		}
		GlobalCacheJcl globalInfo = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Info", String.valueOf(scheduleJcl.getScheduleId())));
		if(null != globalInfo) {
			InfoJclDTO dto = InfoJclDTO.fromJsonToInfoJclDTO(globalInfo.getValue());
			setRanking(scheduleJcl.getScheduleId(), scheduleJcl.getSclassId(), dto);
			return dto;
		}
		//亚赔-让球盘
		GlobalCacheJcl letGoal = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == letGoal) {
			List<LetGoalJcl> letGoals = LetGoalJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildLetGoals(letGoals);
			letGoal = new GlobalCacheJcl();
			letGoal.setId(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
			letGoal.setValue(LetGoalJcl.toJsonArray(letGoals));
			letGoal.persist();
		}
		//亚赔-总分盘
		GlobalCacheJcl totalScore = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == totalScore) {
			List<TotalScoreJcl> totalScores = TotalScoreJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildTotalScores(totalScores);
			totalScore = new GlobalCacheJcl();
			totalScore.setId(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
			totalScore.setValue(TotalScoreJcl.toJsonArray(totalScores));
			totalScore.persist();
		}
		//欧赔
		GlobalCacheJcl standard = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == standard) {
			Collection<StandardJcl> standards = StandardJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildStandards(scheduleJcl, standards);
			standard = new GlobalCacheJcl();
			standard.setId(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
			standard.setValue(StandardJcl.toJsonArray(standards));
			standard.persist();
		}
		//主队近期战绩
		Collection<ScheduleJclDTO> homePreSchedules = analysisJclService.getPreHomeSchedules(scheduleJcl.getScheduleId());
		//客队近期战绩
		Collection<ScheduleJclDTO> guestPreSchedules = analysisJclService.getPreGuestSchedules(scheduleJcl.getScheduleId());
		//主队未来赛事
		Collection<ScheduleJclDTO> homeAfterSchedules = analysisJclService.getAfterHomeSchedules(scheduleJcl.getScheduleId());
		//客队未来赛事
		Collection<ScheduleJclDTO> guestAfterSchedules = analysisJclService.getAfterGuestSchedules(scheduleJcl.getScheduleId());
		//历史交锋
		Collection<ScheduleJclDTO> preClashSchedules = analysisJclService.getPreClashSchedules(scheduleJcl.getScheduleId());
		
		InfoJclDTO dto = new InfoJclDTO();
		ScheduleJclDTO scheduleJclDTO = analysisJclService.buildDTO(scheduleJcl, false,false);
		dto.setSchedule(scheduleJclDTO);
		
		Collection<LetGoalJcl> letGoals = LetGoalJcl.fromJsonArrayToLetGoalJcls(letGoal.getValue());
		dto.setLetGoals(letGoals);
		
		Collection<TotalScoreJcl> totalScores = TotalScoreJcl.fromJsonArrayToTotalScoreJcls(totalScore.getValue());
		dto.setTotalScores(totalScores);
		
		Collection<StandardJcl> standards = StandardJcl.fromJsonArrayToStandardJcls(standard.getValue());
		dto.setStandards(standards);
		
		dto.setHomePreSchedules(homePreSchedules);
		dto.setGuestPreSchedules(guestPreSchedules);
		dto.setHomeAfterSchedules(homeAfterSchedules);
		dto.setGuestAfterSchedules(guestAfterSchedules);
		dto.setPreClashSchedules(preClashSchedules);
		
		globalInfo = new GlobalCacheJcl();
		globalInfo.setId(StringUtil.join("_", "dataAnalysisJcl", "Info", String.valueOf(scheduleJcl.getScheduleId())));
		globalInfo.setValue(dto.toJson());
		globalInfo.persist();
		setRanking(scheduleJcl.getScheduleId(), scheduleJcl.getSclassId(), dto);
		return dto;
	}
	
	/**
	 * 获取即时比分列表
	 * @param day
	 * @param state
	 * @return
	 */
	public List<ScheduleJclDTO> getImmediateScores(String day, int state) {
		List<ScheduleJcl> schedules = ScheduleJcl.findByEventAndDay(day);
		List<ScheduleJclDTO> dtos = new ArrayList<ScheduleJclDTO>();
		for(ScheduleJcl s : schedules) {
			ScheduleJclDTO dto = new ScheduleJclDTO();
			try {
				if(state == 1) {  // 未开
					if(!s.getMatchState().equals("0")) {
						continue;
					}
				}
				if(state == 2) {  // 比赛中
					if(s.getMatchState().equals("0")||s.getMatchState().equals("-1")||s.getMatchState().equals("-4")) {
						continue;
					}
				}
				if(state == 3) {  // 完场
					if(!s.getMatchState().equals("-1")&&!s.getMatchState().equals("-4")) {
						continue;
					}
				}
				SclassJcl sclassJcl = SclassJcl.findSclassJcl(s.getSclassId());
				BeanUtilsEx.copyProperties(dto, s);
				dto.setSclassName(sclassJcl.getNameJ());
				dto.setSclassShortName(sclassJcl.getNameJs());
				dtos.add(dto);
			} catch (Exception e) {
				logger.error("竞彩篮球获取即时比分列表异常", e);
			} 
		}
		return dtos;
	}
	
	/**
	 * 即时比分详细
	 * @param event
	 * @return
	 */
	public ScheduleJclDTO getImmediateScore(String event) {
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		if(null == schedule) {
			return null;
		}
		ScheduleJclDTO dto = new ScheduleJclDTO();
		try {
			SclassJcl sclass = SclassJcl.findSclassJcl(schedule.getSclassId());
			BeanUtilsEx.copyProperties(dto, schedule);
			dto.setSclassName(sclass.getNameJ());
			dto.setSclassShortName(sclass.getNameJs());
		} catch (Exception e) {
			logger.error("竞彩篮球即时比分详细异常", e);
		} 
		return dto;
	}
	
	/**
	 * 进行中比赛查询
	 * @return
	 */
	public List<ScheduleJclDTO> getProcessingMatches() {
		List<ScheduleJcl> schedules = ScheduleJcl.findProcessingMatches();
		List<ScheduleJclDTO> dtos = new ArrayList<ScheduleJclDTO>();
		for(ScheduleJcl s : schedules) {
			ScheduleJclDTO dto = new ScheduleJclDTO();
			try {
				SclassJcl sclassJcl = SclassJcl.findSclassJcl(s.getSclassId());
				BeanUtilsEx.copyProperties(dto, s);
				dto.setSclassName(sclassJcl.getNameJ());
				dto.setSclassShortName(sclassJcl.getNameJs());
				dtos.add(dto);
			} catch (Exception e) {
				logger.error("竞彩篮球进行中比赛查询发生异常", e);
			} 
		}
		return dtos;
	}
	
	/**
	 * 设置联赛排名
	 * @param scheduleID
	 * @param sclassID
	 * @param dto
	 */
	private void setRanking(int scheduleID, int sclassID, InfoJclDTO dto) {
		GlobalCacheJcl ranking = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Ranking", String.valueOf(sclassID)));
		if(null != ranking) {
			dto.setRankings(RankingJclDTO.fromJsonArrayToRankingJclDTO(ranking.getValue()));
		} else {
			Collection<RankingJclDTO> dtos = analysisJclService.getRanking(scheduleID, false);
			dto.setRankings(dtos);
		}
	}
	
	/**
	 * 亚赔-让分盘-设置公司名称
	 * @param letGoals
	 */
	private void buildLetGoals(Collection<LetGoalJcl> letGoalJcls) {
		if(null != letGoalJcls && !letGoalJcls.isEmpty()) {
			for(LetGoalJcl letGoalJcl : letGoalJcls) {
				CompanyJcl companyJcl = CompanyJcl.findCompanyJcl(letGoalJcl.getCompanyId());
				if(null != companyJcl) {
					letGoalJcl.setCompanyName(companyJcl.getCompanyName());
				}
			}
		}
	}
	
	/**
	 * 亚赔-总分盘-设置公司名称
	 * @param letGoals
	 */
	private void buildTotalScores(Collection<TotalScoreJcl> totalScoreJcls) {
		if(null != totalScoreJcls && !totalScoreJcls.isEmpty()) {
			for(TotalScoreJcl totalScoreJcl : totalScoreJcls) {
				CompanyJcl companyJcl = CompanyJcl.findCompanyJcl(totalScoreJcl.getCompanyId());
				if(null != companyJcl) {
					totalScoreJcl.setCompanyName(companyJcl.getCompanyName());
				}
			}
		}
	}
	
	/**
	 * 欧赔-设置公司名称
	 * @param schedule
	 * @param standards
	 */
	private void buildStandards(ScheduleJcl scheduleJcl, Collection<StandardJcl> standardJcls) {
		if(null != standardJcls && !standardJcls.isEmpty()) {
			for(StandardJcl standardJcl : standardJcls) {
				EuropeCompanyJcl europeCompanyJcl = EuropeCompanyJcl.findEuropeCompanyJcl(standardJcl.getCompanyId());
				if(null != europeCompanyJcl) {
					standardJcl.setCompanyName(europeCompanyJcl.getNameC());
				}
				standardJcl.setHomeWin(null == standardJcl.getHomeWin() ? standardJcl.getFirstHomeWin() : standardJcl.getHomeWin());
				standardJcl.setGuestWin(null == standardJcl.getGuestWin() ? standardJcl.getFirstGuestWin() : standardJcl.getGuestWin());
				//设置胜率、负率、凯利指数、返还率
				standardJcl.setHomeWinLv(CalcJclUtil.probability_H(standardJcl.getHomeWin(), standardJcl.getGuestWin()));
				standardJcl.setGuestWinLv(CalcJclUtil.probability_G(standardJcl.getHomeWin(), standardJcl.getGuestWin()));
				standardJcl.setFanHuanLv(CalcJclUtil.fanhuan(standardJcl.getHomeWinLv(), standardJcl.getHomeWin()));
				if(null != scheduleJcl.getAvgH()) {
					standardJcl.setK_h(CalcJclUtil.k_h(standardJcl.getHomeWinLv(), scheduleJcl.getAvgH()));
				}
				if(null != scheduleJcl.getAvgG()) {
					standardJcl.setK_g(CalcJclUtil.k_g(standardJcl.getGuestWinLv(), scheduleJcl.getAvgG()));
				}
			}
		}
	}
	
	/**
	 * 更新数据分析
	 * @param scheduleID
	 */
	public void updateInfo(Integer scheduleID) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == scheduleJcl) {
			return;
		}
		//亚赔-让分盘
		GlobalCacheJcl letGoal = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == letGoal) {
			List<LetGoalJcl> letGoals = LetGoalJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildLetGoals(letGoals);
			letGoal = new GlobalCacheJcl();
			letGoal.setId(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
			letGoal.setValue(LetGoalJcl.toJsonArray(letGoals));
			letGoal.persist();
		}
		//亚赔-总分盘
		GlobalCacheJcl totalScore = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == totalScore) {
			List<TotalScoreJcl> totalScores = TotalScoreJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildTotalScores(totalScores);
			totalScore = new GlobalCacheJcl();
			totalScore.setId(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
			totalScore.setValue(TotalScoreJcl.toJsonArray(totalScores));
			totalScore.persist();
		}
		//欧赔
		GlobalCacheJcl standard = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == standard) {
			Collection<StandardJcl> standards = StandardJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildStandards(scheduleJcl, standards);
			standard = new GlobalCacheJcl();
			standard.setId(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
			standard.setValue(StandardJcl.toJsonArray(standards));
			standard.persist();
		}
		//主队近期战绩
		Collection<ScheduleJclDTO> homePreSchedules = analysisJclService.getPreHomeSchedules(scheduleJcl.getScheduleId());
		//客队近期战绩
		Collection<ScheduleJclDTO> guestPreSchedules = analysisJclService.getPreGuestSchedules(scheduleJcl.getScheduleId());
		//主队未来赛事
		Collection<ScheduleJclDTO> homeAfterSchedules = analysisJclService.getAfterHomeSchedules(scheduleJcl.getScheduleId());
		//客队未来赛事
		Collection<ScheduleJclDTO> guestAfterSchedules = analysisJclService.getAfterGuestSchedules(scheduleJcl.getScheduleId());
		//历史交锋
		Collection<ScheduleJclDTO> preClashSchedules = analysisJclService.getPreClashSchedules(scheduleJcl.getScheduleId());
		
		InfoJclDTO dto = new InfoJclDTO();
		ScheduleJclDTO scheduleJclDTO = analysisJclService.buildDTO(scheduleJcl, true,false);
		dto.setSchedule(scheduleJclDTO);
		
		Collection<LetGoalJcl> letGoals = LetGoalJcl.fromJsonArrayToLetGoalJcls(letGoal.getValue());
		dto.setLetGoals(letGoals);
		
		Collection<TotalScoreJcl> totalScores = TotalScoreJcl.fromJsonArrayToTotalScoreJcls(totalScore.getValue());
		dto.setTotalScores(totalScores);
		
		Collection<StandardJcl> standards = StandardJcl.fromJsonArrayToStandardJcls(standard.getValue());
		dto.setStandards(standards);
		
		dto.setHomePreSchedules(homePreSchedules);
		dto.setGuestPreSchedules(guestPreSchedules);
		dto.setHomeAfterSchedules(homeAfterSchedules);
		dto.setGuestAfterSchedules(guestAfterSchedules);
		dto.setPreClashSchedules(preClashSchedules);
		
		GlobalCacheJcl globalInfo = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Info", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == globalInfo) {
			globalInfo = new GlobalCacheJcl();
			globalInfo.setId(StringUtil.join("_", "dataAnalysisJcl", "Info", String.valueOf(scheduleJcl.getScheduleId())));
			globalInfo.setValue(dto.toJson());
			globalInfo.persist();
		} else {
			globalInfo.setValue(dto.toJson());
			globalInfo.merge();
		}
	}
	
	/**
	 * 根据event查询赛事信息
	 * @param event
	 * @return
	 */
	public ScheduleJclDTO getScheduleDtoByEvent(String event) {
		if (StringUtils.isBlank(event)) {
			return null;
		}
		ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, true);
		if(scheduleJcl==null) {
			return null;
		}
		return analysisJclService.buildDTO(scheduleJcl, true , true);
	}
	
	/**
	 * 查询赛事
	 * @return
	 */
	public Map<String, List<ScheduleJclDTO>> getSchedulesByDay(String day) {
		Map<String, List<ScheduleJclDTO>> results = new LinkedHashMap<String, List<ScheduleJclDTO>>();
		if (StringUtils.isNotBlank(day)) {
			String key = StringUtil.join("_", "dadaanalysis", "schedulesByDayLq", day);
			results = cacheService.get(key);
			if (results==null) {
				results = getSchedules(day);
				if (results!=null) {
					cacheService.set(key, results);
				}
			}
		}
		return results;
	}
	
	public Map<String, List<ScheduleJclDTO>> getSchedules(String day) {
		Map<String, List<ScheduleJclDTO>> results = new LinkedHashMap<String, List<ScheduleJclDTO>>();
		Date matchDate = DateUtil.parse("yyyyMMdd", day);
		calendar.setTime(matchDate);
		calendar.add(Calendar.DATE, 1);
		List<ScheduleJcl> list = ScheduleJcl.findByDay(matchDate, calendar.getTime());
		if (list!=null && list.size()>0) {
			for (ScheduleJcl scheduleJcl : list) {
				String sclassId = String.valueOf(scheduleJcl.getSclassId()); //联赛编号
				List<ScheduleJclDTO> dtoList = results.get(sclassId);
				if (dtoList==null) {
					dtoList = new ArrayList<ScheduleJclDTO>();
				}
				dtoList.add(analysisJclService.buildDTO(scheduleJcl, false, false));
				results.put(sclassId, dtoList);
			}
		}
		return results;
	}
	
	public ClasliAnalysisJclDto findClasliAnalysis(String event) {
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		ClasliAnalysisJclDto dto = new ClasliAnalysisJclDto();
		if(null == schedule) {
			return dto;
		}
		ScheduleJclDTO scheduleDTO = analysisJclService.buildDTO(schedule, true, false);
		int scheduleId = schedule.getScheduleId();
		//历史交锋
		Collection<ScheduleJclDTO> preClashSchedules = analysisJclService.getPreClashSchedules(schedule.getScheduleId());
		//联赛排名
		Collection<RankingJclDTO> rankingDtos = analysisJclService.getRanking(scheduleId, true);
		dto.setSchedule(scheduleDTO);
		dto.setBetRatio(getBetRatioDto(event));
		dto.setBetNum(getBetNumDto(event));
		dto.setPreClashSchedules(preClashSchedules);
		dto.setRankings(rankingDtos);
		dto.setLetgoal(letgoalJclService.getLetgoalJclDtoByCompanyId(scheduleId, 1)); //澳门
		return dto;
	}
	
	public BetRatioDto getBetRatioDto(String event) {
		try {
			String result = analyzeService.getJingcaieventbetcount(event);
			if (StringUtils.isBlank(result)) {
				return null;
			}
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject==null) {
				return null;
			}
			String errorCode = fromObject.getString("errorCode");
			if (StringUtils.equals(errorCode, "0")) {
				JSONObject valueObject = fromObject.getJSONObject("value");
				BetRatioDto dto = new BetRatioDto();
				dto.setSpf(valueObject.getString("spf"));
				dto.setRfspf(valueObject.getString("rfspf"));
				return dto;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BetNumDto getBetNumDto(String event) {
		try {
			String result = analyzeService.getJingcaieventbettotal(event);
			if (StringUtils.isBlank(result)) {
				return null;
			}
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject==null) {
				return null;
			}
			String errorCode = fromObject.getString("errorCode");
			if (StringUtils.equals(errorCode, "0")) {
				JSONObject valueObject = fromObject.getJSONObject("value");
				BetNumDto dto = new BetNumDto();
				dto.setSpf(valueObject.getString("spf"));
				dto.setRfspf(valueObject.getString("rfspf"));
				return dto;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询即时比分
	 * @param state
	 * @return
	 */
	public Map<String, List<ScheduleJclDTO>> findInstantScores(int state) {
		Map<String, List<ScheduleJclDTO>> resultMap = new LinkedHashMap<String, List<ScheduleJclDTO>>();
		if (state == 1) { // 未开赛
			List<String> activedays = commonService.getActivedays("1");
			if (activedays==null||activedays.size()<=0) {
				return null;
			}
			for (String activeday : activedays) {
				List<ScheduleJclDTO> dtos = new ArrayList<ScheduleJclDTO>();
				List<ScheduleJcl> schedules = ScheduleJcl.findByEventAndDay(activeday);
				if (schedules == null || schedules.size() <= 0) {
					continue;
				}
				for (ScheduleJcl schedule : schedules) {
					String matchState = schedule.getMatchState();
					Date matchTime = schedule.getMatchTime();
					Date nowTime = new Date();
					if (matchState == null) {
						continue;
					}
					if (!matchState.equals(MatchStateJcl.weiKai.value)
							&& !matchState.equals(MatchStateJcl.daiDing.value)
							&& !matchState.equals(MatchStateJcl.tuiChi.value)
							&& !matchState.equals(MatchStateJcl.quXiao.value)) {
						continue;
					}
					if (matchTime.before(nowTime) || matchTime.equals(nowTime)) {// 过滤掉比赛开始时间在当前时间之前的场次
						continue;
					}
					ScheduleJclDTO dto = analysisJclService.buildDTO(schedule, true, false);
					dtos.add(dto);
				}
				if (dtos != null && dtos.size() > 0) {
					resultMap.put(activeday, dtos);
				}
			}
		} else if (state == 2) { // 进行中
			List<ScheduleJcl> schedules = ScheduleJcl.findProcessingMatches();
			if (schedules == null || schedules.size() <= 0) {
				return null;
			}
			for (ScheduleJcl schedule : schedules) {
				String event = schedule.getEvent();
				if (StringUtils.isBlank(event)) {
					continue;
				}
				String day = JingCaiUtil.getDayByEvent(event);
				if (StringUtils.isBlank(day)) {
					continue;
				}
				List<ScheduleJclDTO> dtos = resultMap.get(day);
				if (dtos == null) {
					dtos = new ArrayList<ScheduleJclDTO>();
				}
				ScheduleJclDTO dto = analysisJclService.buildDTO(schedule, true, false);
				dtos.add(dto);
				if (dtos != null && dtos.size() > 0) {
					resultMap.put(day, dtos);
				}
			}
		} else if (state == 3) { // 完场
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			List<String> days = new ArrayList<String>();
			days.add(sdf.format(DateUtil.getPreDate(0))); //今天
			days.add(sdf.format(DateUtil.getPreDate(1))); //昨天
			days.add(sdf.format(DateUtil.getPreDate(2))); //前天
			for (String day : days) {
				List<ScheduleJclDTO> dtos = new ArrayList<ScheduleJclDTO>();
				List<ScheduleJcl> schedules = ScheduleJcl.findByEventAndDay(day);
				if (schedules == null || schedules.size() <= 0) {
					continue;
				}
				for (ScheduleJcl schedule : schedules) {
					String matchState = schedule.getMatchState();
					if (matchState == null) {
						continue;
					}
					if (!matchState.equals(MatchStateJcl.wanChang.value)
							&& !matchState.equals(MatchStateJcl.quXiao.value)) {
						continue;
					}
					ScheduleJclDTO dto = analysisJclService.buildDTO(schedule, true, false);
					dtos.add(dto);
				}
				if (dtos != null && dtos.size() > 0) {
					resultMap.put(day, dtos);
				}
			}
		}
		//排序
		if (resultMap!=null&&resultMap.size()>0) {
			for(Map.Entry<String, List<ScheduleJclDTO>> entry : resultMap.entrySet()) {
				String key = entry.getKey();
				List<ScheduleJclDTO> value = entry.getValue();
				sortScheduleJclDtoList(value); //排序
				resultMap.put(key, value);
			}
		}
		return resultMap;
	}
	
	/**
	 * 排序ScheduleJclDTO数组
	 * @param list
	 */
	private static void sortScheduleJclDtoList(List<ScheduleJclDTO> list) {
		Collections.sort(list, new Comparator<ScheduleJclDTO>() {
			@Override
			public int compare(ScheduleJclDTO o1, ScheduleJclDTO o2) {
				if (o1.getEvent().compareTo(o2.getEvent())<0) {
					return -1;
				}
				return 1;
			}
		});
	}
	
	public AnalysisJclDto getAnalysis(String event) {
		long startMills = System.currentTimeMillis();
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		long endMills = System.currentTimeMillis();
		logger.info("竞篮getAnalysis-schedule,用时:"+(endMills-startMills)+",event="+event);
		if(null == schedule) {
			return null;
		}
		AnalysisJclDto dto = getAnalysisDto(schedule);
		return dto;
	}
	
	private AnalysisJclDto getAnalysisDto(ScheduleJcl scheduleJcl) {
		InfoJclDTO infoDTO = getInfoDTO(scheduleJcl);
		if (infoDTO==null) {
			return null;
		}
		AnalysisJclDto dto = new AnalysisJclDto();
		dto.setSchedule(analysisJclService.buildDTO(scheduleJcl, true,false));
		dto.setBetRatio(getBetRatioDto(scheduleJcl.getEvent()));
		dto.setHomePreSchedules(infoDTO.getHomePreSchedules());
		dto.setHomeAfterSchedules(infoDTO.getHomeAfterSchedules());
		dto.setGuestPreSchedules(infoDTO.getGuestPreSchedules());
		dto.setGuestAfterSchedules(infoDTO.getGuestAfterSchedules());
		dto.setPreClashSchedules(infoDTO.getPreClashSchedules());
		dto.setRankings(infoDTO.getRankings());
		return dto;
	}
	
	/**
	 * 获得数据分析DTO
	 * @param schedule
	 * @return
	 */
	private InfoJclDTO getInfoDTO(ScheduleJcl scheduleJcl) {
		GlobalCacheJcl globalInfo = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Info", String.valueOf(scheduleJcl.getScheduleId())));
		if(null != globalInfo) {
			InfoJclDTO dto = InfoJclDTO.fromJsonToInfoJclDTO(globalInfo.getValue());
			setRanking(scheduleJcl.getScheduleId(), scheduleJcl.getSclassId(), dto);
			return dto;
		}
		
		InfoJclDTO dto = getInfo(scheduleJcl.getEvent());
		return dto;
	}
	
	/**
	 * 欧赔
	 * @param schedule
	 * @return
	 */
	public GlobalCacheJcl getStandard(ScheduleJcl scheduleJcl) {
		GlobalCacheJcl standard = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == standard) {
			Collection<StandardJcl> standards = StandardJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildStandards(scheduleJcl, standards);
			standard = new GlobalCacheJcl();
			standard.setId(StringUtil.join("_", "dataAnalysisJcl", "Standard", String.valueOf(scheduleJcl.getScheduleId())));
			standard.setValue(StandardJcl.toJsonArray(standards));
			standard.persist();
		}
		return standard;
	}
	
	/**
	 * 亚赔-让球盘
	 * @param schedule
	 * @return
	 */
	public GlobalCacheJcl getLetGoal(ScheduleJcl scheduleJcl) {
		GlobalCacheJcl letGoal = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == letGoal) {
			List<LetGoalJcl> letGoals = LetGoalJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildLetGoals(letGoals);
			letGoal = new GlobalCacheJcl();
			letGoal.setId(StringUtil.join("_", "dataAnalysisJcl", "LetGoal", String.valueOf(scheduleJcl.getScheduleId())));
			letGoal.setValue(LetGoalJcl.toJsonArray(letGoals));
			letGoal.persist();
		}
		return letGoal;
	}
	
	/**
	 * 亚赔-总分盘
	 * @param schedule
	 * @return
	 */
	public GlobalCacheJcl getTotalScore(ScheduleJcl scheduleJcl) {
		GlobalCacheJcl totalScore = GlobalCacheJcl.findGlobalCache(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
		if(null == totalScore) {
			List<TotalScoreJcl> totalScores = TotalScoreJcl.findByScheduleID(scheduleJcl.getScheduleId());
			buildTotalScores(totalScores);
			totalScore = new GlobalCacheJcl();
			totalScore.setId(StringUtil.join("_", "dataAnalysisJcl", "TotalScore", String.valueOf(scheduleJcl.getScheduleId())));
			totalScore.setValue(TotalScoreJcl.toJsonArray(totalScores));
			totalScore.persist();
		}
		return totalScore;
	}
	
	public List<ScheduleJclDTO> findScheduleByEvents(String events) {
		if (StringUtils.isBlank(events)) {
			return null;
		}
		String[] separator = StringUtils.splitByWholeSeparator(events, ",");
		if (separator==null||separator.length<=0) {
			return null;
		}
		List<ScheduleJclDTO> processingList = new ArrayList<ScheduleJclDTO>();
		List<ScheduleJclDTO> wanchangList = new ArrayList<ScheduleJclDTO>();
		List<ScheduleJclDTO> weikaiList = new ArrayList<ScheduleJclDTO>();
		for (String event : separator) {
			ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, true);
			if(scheduleJcl==null) {
				continue;
			}
			ScheduleJclDTO scheduleJclDTO = analysisJclService.buildDTO(scheduleJcl, true,false);
			if (scheduleJclDTO==null) {
				continue;
			}
			String matchState = scheduleJclDTO.getMatchState();
			if (matchState==null) {
				continue;
			}
			if (matchState.equals(MatchStateJcl.yiJie.value) || matchState.equals(MatchStateJcl.zhongChang.value)
					|| matchState.equals(MatchStateJcl.erJie.value) || matchState.equals(MatchStateJcl.sanJie.value)
					|| matchState.equals(MatchStateJcl.siJie.value) || matchState.equals(MatchStateJcl.zhongDuan.value)) { // 进行中
				processingList.add(scheduleJclDTO);
			}
			if (matchState.equals(MatchStateJcl.wanChang.value) || matchState.equals(MatchStateJcl.quXiao.value)) { // 完场
				wanchangList.add(scheduleJclDTO);
			}
			if (matchState.equals(MatchStateJcl.weiKai.value) || matchState.equals(MatchStateJcl.daiDing.value)
					|| matchState.equals(MatchStateJcl.tuiChi.value)) { // 未开赛
				weikaiList.add(scheduleJclDTO);
			}
		}
		List<ScheduleJclDTO> resultList = new ArrayList<ScheduleJclDTO>();
		resultList.addAll(processingList);
		resultList.addAll(wanchangList);
		resultList.addAll(weikaiList);
		if (resultList==null||resultList.size()<=0) {
			return null;
		}
		return resultList;
	}
	
}
