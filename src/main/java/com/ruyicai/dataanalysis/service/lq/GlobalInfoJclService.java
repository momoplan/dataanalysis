package com.ruyicai.dataanalysis.service.lq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.lq.CompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.EuropeCompanyJcl;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.SclassJcl;
import com.ruyicai.dataanalysis.domain.lq.StandardJcl;
import com.ruyicai.dataanalysis.domain.lq.TotalScoreJcl;
import com.ruyicai.dataanalysis.service.dto.lq.InfoJclDTO;
import com.ruyicai.dataanalysis.service.dto.lq.RankingJclDTO;
import com.ruyicai.dataanalysis.service.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.lq.CalcJclUtil;

@Service
public class GlobalInfoJclService {

	private Logger logger = LoggerFactory.getLogger(GlobalInfoJclService.class);
	
	private Calendar calendar = Calendar.getInstance();
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private CacheService cacheService;
	
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
		ScheduleJclDTO scheduleJclDTO = analysisJclService.buildDTO(scheduleJcl);
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
		ScheduleJclDTO scheduleJclDTO = analysisJclService.buildDTO(scheduleJcl);
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
		return analysisJclService.buildDTO(scheduleJcl);
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
				dtoList.add(analysisJclService.buildDTO(scheduleJcl));
				results.put(sclassId, dtoList);
			}
		}
		return results;
	}
	
}
