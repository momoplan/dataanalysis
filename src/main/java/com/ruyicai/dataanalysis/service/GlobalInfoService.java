package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import com.ruyicai.dataanalysis.domain.Company;
import com.ruyicai.dataanalysis.domain.DetailResult;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.LetGoalDetail;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.domain.StandardDetail;
import com.ruyicai.dataanalysis.dto.AnalysisDto;
import com.ruyicai.dataanalysis.dto.BetRatioDto;
import com.ruyicai.dataanalysis.dto.InfoDTO;
import com.ruyicai.dataanalysis.dto.RankingDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.service.back.AnalyzeService;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.Page;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.zc.ZuCaiUtil;
import com.ruyicai.dataanalysis.util.zq.CalcUtil;

@Service
public class GlobalInfoService {

	private Logger logger = LoggerFactory.getLogger(GlobalInfoService.class);
	
	private Calendar calendar = Calendar.getInstance();
	
	@Autowired
	private AnalyzeService analyzeService;
	
	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private AsyncService asyncService;
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * 竞彩足球数据分析
	 * @param event
	 * @return
	 */
	public InfoDTO getInfo(String event) {
		long startMills = System.currentTimeMillis();
		Schedule schedule = Schedule.findByEvent(event, true);
		long endMills = System.currentTimeMillis();
		logger.info("竞足getInfo-schedule,用时:"+(endMills-startMills)+",event="+event);
		if(null == schedule) {
			return null;
		}
		InfoDTO dto = getInfoDTO(schedule);
		return dto;
	}
	
	/**
	 * 足彩数据分析
	 * @param zcEvent
	 * @return
	 */
	public InfoDTO getZcInfo(String zcEvent) {
		long startMillis = System.currentTimeMillis();
		String lotNo = ZuCaiUtil.getLotNoByZcEvent(zcEvent); //彩种编号
		if (StringUtil.isEmpty(lotNo)) {
			return null;
		}
		Schedule schedule = ZuCaiUtil.getZcScheduleByLotNo(lotNo, zcEvent);
		if(schedule==null) {
			return null;
		}
		InfoDTO dto = getInfoDTO(schedule);
		long endMillis = System.currentTimeMillis();
		logger.info("查询足彩数据分析,用时:"+(endMillis-startMillis)+",zcEvent="+zcEvent);
		return dto;
	}
	
	/**
	 * 北单数据分析
	 * @param zcEvent
	 * @return
	 */
	public InfoDTO getBdInfo(String bdEvent) {
		Schedule schedule = Schedule.findByBdEvent(bdEvent);
		if(schedule==null) {
			return null;
		}
		InfoDTO dto = getInfoDTO(schedule);
		return dto;
	}
	
	/**
	 * 获得数据分析DTO
	 * @param schedule
	 * @return
	 */
	private InfoDTO getInfoDTO(Schedule schedule) {
		int scheduleId = schedule.getScheduleID();
		String key = StringUtil.join("_", "dataanalysis", "Info", String.valueOf(scheduleId));
		GlobalCache globalInfo = GlobalCache.findGlobalCache(key);
		if(null != globalInfo) {
			InfoDTO dto = InfoDTO.fromJsonToInfoDTO(globalInfo.getValue());
			dto.setRankings(getRankingDtos(scheduleId, schedule.getSclassID()));
			return dto;
		}
		InfoDTO dto = getUpdateInfoDTO(schedule);
		dto.setRankings(getRankingDtos(scheduleId, schedule.getSclassID()));
		asyncService.saveGlobalCache(key, dto.toJson());
		return dto;
	}

	private Collection<RankingDTO> getRankingDtos(int scheduleID, int sclassID) {
		String key = StringUtil.join("_", "dataanalysis", "Ranking", String.valueOf(sclassID));
		GlobalCache ranking = GlobalCache.findGlobalCache(key);
		if(null != ranking) {
			return RankingDTO.fromJsonArrayToRankingDTO(ranking.getValue());
		} else {
			Collection<RankingDTO> dtos = analysisService.getRanking(scheduleID, false);
			return dtos;
		}
	}
	
	public InfoDTO getUpdateInfoDTO(Schedule schedule) {
		int scheduleId = schedule.getScheduleID();
		GlobalCache letGoal = getLetGoal(schedule);
		GlobalCache standard = getStandard(schedule);
		long startmillis2 = System.currentTimeMillis();
		Collection<ScheduleDTO> homePreSchedules = analysisService.getPreHomeSchedules(scheduleId, schedule);
		Collection<ScheduleDTO> guestPreSchedules = analysisService.getPreGuestSchedules(scheduleId, schedule);
		Collection<ScheduleDTO> homeAfterSchedules = analysisService.getAfterHomeSchedules(scheduleId, schedule);
		Collection<ScheduleDTO> guestAfterSchedules = analysisService.getAfterGuestSchedules(scheduleId, schedule);
		Collection<ScheduleDTO> preClashSchedules = analysisService.getPreClashSchedules(scheduleId, schedule);
		long endmillis2 = System.currentTimeMillis();
		logger.info("getUpdateInfoDTO,scheduleID="+scheduleId+",获取赛事用时:"+(endmillis2-startmillis2));
		
		InfoDTO dto = new InfoDTO();
		ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule);
		dto.setSchedule(scheduleDTO);
		
		Collection<LetGoal> letGoals = LetGoal.fromJsonArrayToLetGoals(letGoal.getValue());
		dto.setLetGoals(letGoals);
		
		Collection<Standard> standards = Standard.fromJsonArrayToStandards(standard.getValue());
		dto.setStandards(standards);
		
		dto.setHomePreSchedules(homePreSchedules);
		dto.setGuestPreSchedules(guestPreSchedules);
		dto.setHomeAfterSchedules(homeAfterSchedules);
		dto.setGuestAfterSchedules(guestAfterSchedules);
		dto.setPreClashSchedules(preClashSchedules);
		
		return dto;
	}
	
	public GlobalCache getLetGoal(Schedule schedule) {
		int scheduleId = schedule.getScheduleID();
		String letgoalKey = StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(scheduleId));
		GlobalCache letGoal = GlobalCache.findGlobalCache(letgoalKey);
		if(null == letGoal) {
			List<LetGoal> letGoals = LetGoal.findByScheduleID(scheduleId);
			buildLetGoals(letGoals);
			letGoal = new GlobalCache();
			letGoal.setId(letgoalKey);
			letGoal.setValue(LetGoal.toJsonArray(letGoals));
			letGoal.persist();
		}
		return letGoal;
	}
	
	public GlobalCache getStandard(Schedule schedule) {
		int scheduleId = schedule.getScheduleID();
		String stardardKey = StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(scheduleId));
		GlobalCache standard = GlobalCache.findGlobalCache(stardardKey);
		if(null == standard) {
			Collection<Standard> standards = Standard.findByScheduleID(scheduleId);
			buildStandards(schedule, standards);
			standard = new GlobalCache();
			standard.setId(stardardKey);
			standard.setValue(Standard.toJsonArray(standards));
			standard.persist();
		}
		return standard;
	}
	
	public void buildStandards(Schedule schedule, Collection<Standard> standards) {
		if(null != standards && !standards.isEmpty()) {
			for(Standard standard : standards) {
				EuropeCompany company = EuropeCompany.findEuropeCompany(standard.getCompanyID());
				if(null != company) {
					standard.setCompanyName(company.getName_Cn());
					standard.setCompanyName_e(company.getName_E());
					standard.setIsPrimary(company.getIsPrimary());
					standard.setIsExchange(company.getIsExchange());
				}
				standard.setHomeWin(null == standard.getHomeWin() ? standard.getFirstHomeWin() : standard.getHomeWin());
				standard.setStandoff(null == standard.getStandoff() ? standard.getFirstStandoff() : standard.getStandoff());
				standard.setGuestWin(null == standard.getGuestWin() ? standard.getFirstGuestWin() : standard.getGuestWin());
				standard.setHomeWinLu(CalcUtil.probability_H(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setStandoffLu(CalcUtil.probability_S(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setGuestWinLu(CalcUtil.probability_G(standard.getHomeWin(), standard.getStandoff(), standard.getGuestWin()));
				standard.setFanHuanLu(CalcUtil.fanhuan(standard.getHomeWinLu(), standard.getHomeWin()));
				if(null != schedule.getAvgH()) {
					standard.setK_h(CalcUtil.k_h(standard.getHomeWin(), schedule));
				}
				if(null != schedule.getAvgS()) {
					standard.setK_s(CalcUtil.k_s(standard.getStandoff(), schedule));
				}
				if(null != schedule.getAvgG()) {
					standard.setK_g(CalcUtil.k_g(standard.getGuestWin(), schedule));
				}
			}
		}
	}

	public void buildLetGoals(Collection<LetGoal> letGoals) {
		if(null != letGoals && !letGoals.isEmpty()) {
			for(LetGoal letGoal : letGoals) {
				Company company = Company.findCompany(letGoal.getCompanyID());
				if(null != company) {
					letGoal.setCompanyName(company.getName_Cn());
					letGoal.setCompanyName_e(company.getName_E());
				}
				letGoal.setFirstGoal_name(CalcUtil.handicap(letGoal.getFirstGoal()));
				letGoal.setGoal_name(CalcUtil.handicap(letGoal.getGoal()));
			}
		}
	}
	
	public void updateInfo(Integer scheduleId) {
		logger.info("更新球队信息,scheduleId:{}", new Integer[] {scheduleId});
		long startmillis = System.currentTimeMillis();
		Schedule schedule = Schedule.findSchedule(scheduleId, true);
		if(null == schedule) {
			return;
		}
		InfoDTO dto = getUpdateInfoDTO(schedule);
		
		String key = StringUtil.join("_", "dataanalysis", "Info", String.valueOf(scheduleId));
		GlobalCache globalInfo = GlobalCache.findGlobalCache(key);
		if(null == globalInfo) {
			globalInfo = new GlobalCache();
			globalInfo.setId(key);
			globalInfo.setValue(dto.toJson());
			globalInfo.persist();
		} else {
			globalInfo.setValue(dto.toJson());
			globalInfo.merge();
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新球队信息,scheduleId:{},共用时{}", new String[] {String.valueOf(scheduleId), String.valueOf(endmillis - startmillis)});
	}
	
	public List<ScheduleDTO> getImmediateScores(String day) {
		logger.info("开始获取即时比分数据, day:{}", new String[] {day});
		List<Schedule> schedules = Schedule.findByEventAndDay(day);
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		for(Schedule s : schedules) {
			ScheduleDTO dto = new ScheduleDTO();
			try {
				Sclass sclass = Sclass.findSclass(s.getSclassID());
				BeanUtilsEx.copyProperties(dto, s);
				dto.setSclassName(sclass.getName_J());
				dto.setSclassName_j(sclass.getName_JS());
				
				int scheduleId = s.getScheduleID();
				String key = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(scheduleId));
				GlobalCache globalCache = GlobalCache.findGlobalCache(key);
				if(null == globalCache) {
					List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleId);
					globalCache = new GlobalCache();
					globalCache.setId(key);
					globalCache.setValue(DetailResult.toJsonArray(detailResults));
					globalCache.persist();
					dto.setDetailResults(detailResults);
				} else {
					dto.setDetailResults(DetailResult.fromJsonArrayToDetailResults(globalCache.getValue()));
				}
				dtos.add(dto);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} 
		}
		return dtos;
	}
	
	public List<ScheduleDTO> getImmediateScores(String day, int state) {
		//logger.info("开始获取即时比分数据, day:{},state:{}", new String[] {day, String.valueOf(state)});
		List<Schedule> schedules = Schedule.findByEventAndDay(day);
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		for(Schedule s : schedules) {
			ScheduleDTO dto = new ScheduleDTO();
			try {
				if(state == 1) {  // 未开
					if(s.getMatchState() != 0) {
						continue;
					}
				}
				if(state == 2) {  // 比赛中
					if(s.getMatchState() == 0 || s.getMatchState() == -1 || s.getMatchState() == -10) {
						continue;
					}
				}
				if(state == 3) {  // 完场
					if(s.getMatchState() != -1 && s.getMatchState() != -10) {
						continue;
					}
				}
				Sclass sclass = Sclass.findSclass(s.getSclassID());
				BeanUtilsEx.copyProperties(dto, s);
				dto.setSclassName(sclass.getName_J());
				dto.setSclassName_j(sclass.getName_JS());
				
				int scheduleId = s.getScheduleID();
				String key = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(scheduleId));
				GlobalCache globalCache = GlobalCache.findGlobalCache(key);
				if(null == globalCache) {
					List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleId);
					globalCache = new GlobalCache();
					globalCache.setId(key);
					globalCache.setValue(DetailResult.toJsonArray(detailResults));
					globalCache.persist();
					dto.setDetailResults(detailResults);
				} else {
					dto.setDetailResults(DetailResult.fromJsonArrayToDetailResults(globalCache.getValue()));
				}
				dtos.add(dto);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} 
		}
		return dtos;
	}
	
	public ScheduleDTO getImmediateScore(String event) {
		logger.info("开始获取即时比分数据, event:{}", new String[] {event});
		Schedule schedule = Schedule.findByEvent(event, true);
		if(null == schedule) {
			return null;
		}
		ScheduleDTO dto = new ScheduleDTO();
		try {
			Sclass sclass = Sclass.findSclass(schedule.getSclassID());
			BeanUtilsEx.copyProperties(dto, schedule);
			dto.setSclassName(sclass.getName_J());
			dto.setSclassName_j(sclass.getName_JS());
			
			int scheduleId = schedule.getScheduleID();
			String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(scheduleId));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			if(null == globalCache) {
				List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleId);
				globalCache = new GlobalCache();
				globalCache.setId(id);
				globalCache.setValue(DetailResult.toJsonArray(detailResults));
				globalCache.persist();
				dto.setDetailResults(detailResults);
			} else {
				dto.setDetailResults(DetailResult.fromJsonArrayToDetailResults(globalCache.getValue()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return dto;
	}
	
	public void updateImmediateScore(String event) {
		logger.info("开始更新即时比分数据, event:{}", new String[] {event});
		Schedule schedule = Schedule.findByEvent(event, true);
		if(null == schedule) {
			return;
		}
		try {
			int scheduleId = schedule.getScheduleID();
			String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(scheduleId));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleId);
			if(null == globalCache) {
				globalCache = new GlobalCache();
				globalCache.setId(id);
				globalCache.setValue(DetailResult.toJsonArray(detailResults));
				globalCache.persist();
			} else {
				globalCache.setValue(DetailResult.toJsonArray(detailResults));
				globalCache.merge();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
	}
	
	/**
	 * 进行中的比赛查询
	 * @return
	 */
	public List<ScheduleDTO> getProcessingMatches() {
		List<Schedule> schedules = Schedule.findProcessingMatches();
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		for(Schedule s : schedules) {
			ScheduleDTO dto = new ScheduleDTO();
			try {
				Sclass sclass = Sclass.findSclass(s.getSclassID());
				BeanUtilsEx.copyProperties(dto, s);
				dto.setSclassName(sclass.getName_J());
				dto.setSclassName_j(sclass.getName_JS());
				dtos.add(dto);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} 
		}
		return dtos;
	}
	
	/**
	 * 根据event查询竞彩赛事信息
	 * @param event
	 * @return
	 */
	public ScheduleDTO getScheduleDtoByEvent(String event) {
		if (StringUtils.isBlank(event)) {
			return null;
		}
		Schedule schedule = Schedule.findByEvent(event, true);
		if(schedule==null) {
			return null;
		}
		return analysisService.buildDTO(schedule);
	}
	
	/**
	 * 查询欧赔变化
	 * @param oddsId
	 * @return
	 */
	public void getStandardDetails(String oddsId, Page<StandardDetail> page) {
		if (StringUtils.isBlank(oddsId)) {
			return ;
		}
		StandardDetail.findByOddsId(Integer.valueOf(oddsId), page);
	}
	
	/**
	 * 查询亚赔变化
	 * @param oddsId
	 * @return
	 */
	public void getLetGoalDetails(String oddsId, Page<LetGoalDetail> page) {
		if (StringUtils.isBlank(oddsId)) {
			return ;
		}
		LetGoalDetail.findByOddsId(Integer.valueOf(oddsId), page);
		buildLetGoalDetails(page.getList());
	}
	
	private void buildLetGoalDetails(List<LetGoalDetail> details) {
		if (details!=null&&!details.isEmpty()) {
			for (LetGoalDetail letGoalDetail : details) {
				letGoalDetail.setGoalName(CalcUtil.handicap(letGoalDetail.getGoal()));
			}
		}
	}
	
	/**
	 * 查询赛事
	 * @return
	 */
	public Map<String, List<ScheduleDTO>> getSchedulesByDay(String day) {
		Map<String, List<ScheduleDTO>> results = new LinkedHashMap<String, List<ScheduleDTO>>();
		if (StringUtils.isNotBlank(day)) {
			String key = StringUtil.join("_", "dadaanalysis", "schedulesByDayZq", day);
			results = cacheService.get(key);
			if (results==null) {
				results = getSchedules(day, 0);
				if (results!=null) {
					cacheService.set(key, results);
				}
			}
		}
		return results;
	}
	
	public Map<String, List<ScheduleDTO>> getSchedules(String day, int scheduleId) {
		Map<String, List<ScheduleDTO>> results = new LinkedHashMap<String, List<ScheduleDTO>>();
		Date matchDate = DateUtil.parse("yyyyMMdd", day);
		calendar.setTime(matchDate);
		calendar.add(Calendar.DATE, 1);
		long startmillis = System.currentTimeMillis();
		List<Schedule> list = Schedule.findByDay(matchDate, calendar.getTime(), true);
		long endmillis = System.currentTimeMillis();
		logger.info("Schedule.findByDay,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId);
		if (list!=null && list.size()>0) {
			long startmillis2 = System.currentTimeMillis();
			for (Schedule schedule : list) {
				String sclassID = String.valueOf(schedule.getSclassID()); //联赛编号
				List<ScheduleDTO> dtoList = results.get(sclassID);
				if (dtoList==null) {
					dtoList = new ArrayList<ScheduleDTO>();
				}
				dtoList.add(analysisService.buildDTO(schedule));
				results.put(sclassID, dtoList);
			}
			long endmillis2 = System.currentTimeMillis();
			logger.info("getSchedules-buildDTO,用时:"+(endmillis2-startmillis2)+",size="+list.size());
		}
		return results;
	}
	
	public AnalysisDto getAnalysis(String event) {
		long startMills = System.currentTimeMillis();
		Schedule schedule = Schedule.findByEvent(event, true);
		long endMills = System.currentTimeMillis();
		logger.info("竞足getAnalysis-schedule,用时:"+(endMills-startMills)+",event="+event);
		if(null == schedule) {
			return null;
		}
		AnalysisDto dto = getAnalysisDto(schedule);
		return dto;
	}

	private AnalysisDto getAnalysisDto(Schedule schedule) {
		InfoDTO infoDTO = getInfoDTO(schedule);
		if (infoDTO==null) {
			return null;
		}
		AnalysisDto dto = new AnalysisDto();
		dto.setSchedule(infoDTO.getSchedule());
		dto.setBetRatio(getBetRatioDto(schedule.getEvent()));
		dto.setHomePreSchedules(infoDTO.getHomePreSchedules());
		dto.setHomeAfterSchedules(infoDTO.getHomeAfterSchedules());
		dto.setGuestPreSchedules(infoDTO.getGuestPreSchedules());
		dto.setGuestAfterSchedules(infoDTO.getGuestAfterSchedules());
		dto.setPreClashSchedules(infoDTO.getPreClashSchedules());
		dto.setRankings(getRankingDtos(schedule.getScheduleID(), schedule.getSclassID()));
		return dto;
	}
	
	private BetRatioDto getBetRatioDto(String event) {
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
	
}
