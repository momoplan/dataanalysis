package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Company;
import com.ruyicai.dataanalysis.domain.DetailResult;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.service.dto.InfoDTO;
import com.ruyicai.dataanalysis.service.dto.RankingDTO;
import com.ruyicai.dataanalysis.service.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.CalcUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.ZuCaiUtil;

@Service
public class GlobalInfoService {

	private Logger logger = LoggerFactory.getLogger(GlobalInfoService.class);
	
	@Autowired
	private AnalysisService analysisService;
	
	/**
	 * 竞彩足球数据分析
	 * @param event
	 * @return
	 */
	public InfoDTO getInfo(String event) {
		Schedule schedule = Schedule.findByEvent(event);
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
		String lotNo = ZuCaiUtil.getLotNoByZcEvent(zcEvent); //彩种编号
		if (StringUtil.isEmpty(lotNo)) {
			return null;
		}
		Schedule schedule = ZuCaiUtil.getZcScheduleByLotNo(lotNo, zcEvent);
		if(schedule==null) {
			return null;
		}
		InfoDTO dto = getInfoDTO(schedule);
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
		GlobalCache globalInfo = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
		if(null != globalInfo) {
			InfoDTO dto = InfoDTO.fromJsonToInfoDTO(globalInfo.getValue());
			setRanking(schedule.getScheduleID(), schedule.getSclassID(), dto);
			return dto;
		}
		GlobalCache letGoal = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(schedule.getScheduleID())));
		if(null == letGoal) {
			List<LetGoal> letGoals = LetGoal.findByScheduleID(schedule.getScheduleID());
			buildLetGoals(letGoals);
			letGoal = new GlobalCache();
			letGoal.setId(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(schedule.getScheduleID())));
			letGoal.setValue(LetGoal.toJsonArray(letGoals));
			letGoal.persist();
		}
		GlobalCache standard = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(schedule.getScheduleID())));
		if(null == standard) {
			Collection<Standard> standards = Standard.findByScheduleID(schedule.getScheduleID());
			buildStandards(schedule, standards);
			standard = new GlobalCache();
			standard.setId(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(schedule.getScheduleID())));
			standard.setValue(Standard.toJsonArray(standards));
			standard.persist();
		}
		Collection<ScheduleDTO> homePreSchedules = analysisService.getPreHomeSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> guestPreSchedules = analysisService.getPreGuestSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> homeAfterSchedules = analysisService.getAfterHomeSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> guestAfterSchedules = analysisService.getAfterGuestSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> preClashSchedules = analysisService.getPreClashSchedules(schedule.getScheduleID());
		
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
		
		globalInfo = new GlobalCache();
		globalInfo.setId(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
		globalInfo.setValue(dto.toJson());
		globalInfo.persist();
		setRanking(schedule.getScheduleID(), schedule.getSclassID(), dto);
		return dto;
	}

	private void setRanking(int scheduleID, int sclassID, InfoDTO dto) {
		GlobalCache ranking = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Ranking", String.valueOf(sclassID)));
		if(null != ranking) {
			dto.setRankings(RankingDTO.fromJsonArrayToRankingDTO(ranking.getValue()));
		} else {
			Collection<RankingDTO> dtos = analysisService.getRanking(scheduleID, false);
			dto.setRankings(dtos);
		}
	}
	
	private void buildStandards(Schedule schedule, Collection<Standard> standards) {
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
					standard.setK_h(CalcUtil.k_h(standard.getHomeWinLu(), schedule.getAvgH()));
				}
				if(null != schedule.getAvgS()) {
					standard.setK_s(CalcUtil.k_s(standard.getStandoffLu(), schedule.getAvgS()));
				}
				if(null != schedule.getAvgG()) {
					standard.setK_g(CalcUtil.k_g(standard.getGuestWinLu(), schedule.getAvgG()));
				}
			}
		}
	}

	private void buildLetGoals(Collection<LetGoal> letGoals) {
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
	
	public void updateInfo(Integer scheduleID) {
		logger.info("更新球队信息,scheduleID:{}", new Integer[] {scheduleID});
		long startmillis = System.currentTimeMillis();
		Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return;
		}
		GlobalCache letGoal = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(schedule.getScheduleID())));
		if(null == letGoal) {
			List<LetGoal> letGoals = LetGoal.findByScheduleID(schedule.getScheduleID());
			buildLetGoals(letGoals);
			letGoal = new GlobalCache();
			letGoal.setId(StringUtil.join("_", "dataanalysis", "LetGoal", String.valueOf(schedule.getScheduleID())));
			letGoal.setValue(LetGoal.toJsonArray(letGoals));
			letGoal.persist();
		} 
		GlobalCache standard = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(schedule.getScheduleID())));
		if(null == standard) {
			Collection<Standard> standards = Standard.findByScheduleID(schedule.getScheduleID());
			buildStandards(schedule, standards);
			standard = new GlobalCache();
			standard.setId(StringUtil.join("_", "dataanalysis", "Standard", String.valueOf(schedule.getScheduleID())));
			standard.setValue(Standard.toJsonArray(standards));
			standard.persist();
		}
		Collection<ScheduleDTO> homePreSchedules = analysisService.getPreHomeSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> guestPreSchedules = analysisService.getPreGuestSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> homeAfterSchedules = analysisService.getAfterHomeSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> guestAfterSchedules = analysisService.getAfterGuestSchedules(schedule.getScheduleID());
		Collection<ScheduleDTO> preClashSchedules = analysisService.getPreClashSchedules(schedule.getScheduleID());
		
		InfoDTO dto = new InfoDTO();
		ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule);
		dto.setSchedule(scheduleDTO);
		
		Collection<LetGoal> letGoals = LetGoal.fromJsonArrayToLetGoals(letGoal.getValue());
		buildLetGoals(letGoals);
		dto.setLetGoals(letGoals);
		
		Collection<Standard> standards = Standard.fromJsonArrayToStandards(standard.getValue());
		dto.setStandards(standards);
		
		dto.setHomePreSchedules(homePreSchedules);
		dto.setGuestPreSchedules(guestPreSchedules);
		dto.setHomeAfterSchedules(homeAfterSchedules);
		dto.setGuestAfterSchedules(guestAfterSchedules);
		dto.setPreClashSchedules(preClashSchedules);
		
		GlobalCache globalInfo = GlobalCache.findGlobalCache(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
		if(null == globalInfo) {
			globalInfo = new GlobalCache();
			globalInfo.setId(StringUtil.join("_", "dataanalysis", "Info", String.valueOf(schedule.getScheduleID())));
			globalInfo.setValue(dto.toJson());
			globalInfo.persist();
		} else {
			globalInfo.setValue(dto.toJson());
			globalInfo.merge();
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新球队信息,scheduleID:{},共用时{}", new String[] {String.valueOf(scheduleID), String.valueOf(endmillis - startmillis)});
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
				String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(s.getScheduleID()));
				GlobalCache globalCache = GlobalCache.findGlobalCache(id);
				if(null == globalCache) {
					List<DetailResult> detailResults = DetailResult.findDetailResults(s.getScheduleID());
					globalCache = new GlobalCache();
					globalCache.setId(id);
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
		logger.info("开始获取即时比分数据, day:{},state:{}", new String[] {day, String.valueOf(state)});
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
				String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(s.getScheduleID()));
				GlobalCache globalCache = GlobalCache.findGlobalCache(id);
				if(null == globalCache) {
					List<DetailResult> detailResults = DetailResult.findDetailResults(s.getScheduleID());
					globalCache = new GlobalCache();
					globalCache.setId(id);
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
		Schedule schedule = Schedule.findByEvent(event);
		if(null == schedule) {
			return null;
		}
		ScheduleDTO dto = new ScheduleDTO();
		try {
			Sclass sclass = Sclass.findSclass(schedule.getSclassID());
			BeanUtilsEx.copyProperties(dto, schedule);
			dto.setSclassName(sclass.getName_J());
			dto.setSclassName_j(sclass.getName_JS());
			String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(schedule.getScheduleID()));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			if(null == globalCache) {
				List<DetailResult> detailResults = DetailResult.findDetailResults(schedule.getScheduleID());
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
		Schedule schedule = Schedule.findByEvent(event);
		if(null == schedule) {
			return;
		}
		try {
			String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(schedule.getScheduleID()));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			List<DetailResult> detailResults = DetailResult.findDetailResults(schedule.getScheduleID());
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
	
}
