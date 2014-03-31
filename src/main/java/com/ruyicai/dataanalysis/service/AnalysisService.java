package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.dto.RankingDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class AnalysisService {

	private Logger logger = LoggerFactory.getLogger(AnalysisService.class);
	
	public void updateAllRanking() {
		logger.info("开始更新所有联赛排名");
		long startmillis = System.currentTimeMillis();
		List<Sclass> sclasses = Sclass.findAllSclasses();
		logger.info("联赛size:{}", new Integer[] {sclasses.size()});
		for(Sclass sclass : sclasses) {
			getRanking(false, sclass.getSclassID());
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新所有联赛排名结束, 共用时 " + (endmillis - startmillis));
	}
	
	public Collection<RankingDTO> getRanking(int scheduleID, boolean ischeckCache) {
		logger.info("开始获取赛事球队联赛排名, scheduleID:{}", new Integer[] {scheduleID});
		Schedule schedule = Schedule.findSchedule(scheduleID, true);
		if(null == schedule) {
			return new ArrayList<RankingDTO>();
		}
		return getRanking(ischeckCache, schedule.getSclassID());
	}

	public Collection<RankingDTO> getRanking(boolean ischeckCache,
			int sclassID) {
		Sclass sclass = Sclass.findSclass(sclassID);
		if(null == sclass) {
			return new ArrayList<RankingDTO>();
		}
		if(null != sclass.getIsRanking() && sclass.getIsRanking() == 1) {
			return new ArrayList<RankingDTO>();
		}
		Integer kind = sclass.getKind(); //1:联赛；2:杯赛
		if (kind!=null && kind==2) { //杯赛不进行排名
			return new ArrayList<RankingDTO>();
		}
		if(ischeckCache) {
			String id = StringUtil.join("_", "dataanalysis", "Ranking", String.valueOf(sclassID));
			GlobalCache globalCache = GlobalCache.findGlobalCache(id);
			if(null != globalCache) {
				return RankingDTO.fromJsonArrayToRankingDTO(globalCache.getValue());
			}
		}
		List<Schedule> schedules = Schedule.findBySclassID(sclassID, sclass.getCurr_matchSeason());
		Map<Integer, RankingDTO> map = new HashMap<Integer, RankingDTO>();
		doCalc(schedules, map);
		return doRanking(sclassID, map);
	}

	private Collection<RankingDTO> doRanking(int sclassID, Map<Integer, RankingDTO> map) {
		List<RankingDTO> list = new LinkedList<RankingDTO>();
		for(Entry<Integer, RankingDTO> entry : map.entrySet()) {
			RankingDTO dto = entry.getValue();
			dto.setGoalDifference(dto.getGoinBall() - dto.getLoseBall());
			list.add(entry.getValue());
		}
		Collections.sort(list);
		for(int i = 1; i <= list.size(); i ++) {
			RankingDTO dto = list.get(i - 1);
			dto.setRanking(i);
		}
		String id = StringUtil.join("_", "dataanalysis", "Ranking", String.valueOf(sclassID));
		GlobalCache globalCache = GlobalCache.findGlobalCache(id);
		if(null == globalCache) {
			globalCache = new GlobalCache();
			globalCache.setId(id);
			globalCache.setValue(RankingDTO.toJsonArray(list));
			globalCache.persist();
		} else {
			globalCache.setValue(RankingDTO.toJsonArray(list));
			globalCache.merge();
		}
		return list;
	}

	private void doCalc(List<Schedule> schedules, Map<Integer, RankingDTO> map) {
		for(Schedule s : schedules) {
			int homeTeamID = s.getHomeTeamID();
			int homeScore = s.getHomeScore();
			String homeName = s.getHomeTeam();
			int guestScore = s.getGuestScore();
			int guestTeamID = s.getGuestTeamID();
			String guestName = s.getGuestTeam();
			RankingDTO homeRanking = map.get(homeTeamID);
			if(null == homeRanking) {
				homeRanking = new RankingDTO();
				homeRanking.setTeamID(homeTeamID);
				homeRanking.setTeamName(homeName);
				map.put(homeTeamID, homeRanking);
			}
			RankingDTO guestRanking = map.get(guestTeamID);
			if(null == guestRanking) {
				guestRanking = new RankingDTO();
				guestRanking.setTeamID(guestTeamID);
				guestRanking.setTeamName(guestName);
				map.put(guestTeamID, guestRanking);
			}
			if(MatchState.WANCHANG.value == s.getMatchState()) {
				homeRanking.setMatchcount(homeRanking.getMatchcount() + 1);
				guestRanking.setMatchcount(guestRanking.getMatchcount() + 1);
				if(homeScore == guestScore) {
					homeRanking.setStandoff(homeRanking.getStandoff() + 1);
					homeRanking.setIntegral(homeRanking.getIntegral() + 1);
					guestRanking.setStandoff(guestRanking.getStandoff() + 1);
					guestRanking.setIntegral(guestRanking.getIntegral() + 1);
				}
				if(homeScore > guestScore) {
					homeRanking.setWin(homeRanking.getWin() + 1);
					homeRanking.setIntegral(homeRanking.getIntegral() + 3);
					guestRanking.setLose(guestRanking.getLose() + 1);
				}
				if(homeScore < guestScore) {
					homeRanking.setLose(homeRanking.getLose() + 1);
					guestRanking.setWin(guestRanking.getWin() + 1);
					guestRanking.setIntegral(guestRanking.getIntegral() + 3);
				}
			}
			homeRanking.setGoinBall(homeRanking.getGoinBall() + homeScore);
			homeRanking.setLoseBall(homeRanking.getLoseBall() + guestScore);
			guestRanking.setGoinBall(guestRanking.getGoinBall() + guestScore);
			guestRanking.setLoseBall(guestRanking.getLoseBall() + homeScore);
		}
	}
	
	public Collection<ScheduleDTO> getPreHomeSchedules(int scheduleID, Schedule schedule) {
		if (schedule==null) {
			schedule = Schedule.findSchedule(scheduleID, true);
		}
		if(schedule==null) {
			return new ArrayList<ScheduleDTO>();
		}
		/*Schedule Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleDTO>();
		}*/
		Integer teamId = schedule.getHomeTeamID();
		if (StringUtils.equals(schedule.getTurn(), "1")) {
			teamId = schedule.getGuestTeamID();
		}
		List<Schedule> schedules = Schedule.findPreSchedules(teamId, schedule.getMatchTime());
		return buildDTOS(schedules);
	}

	public Collection<ScheduleDTO> getPreGuestSchedules(int scheduleID, Schedule schedule) {
		if (schedule==null) {
			schedule = Schedule.findSchedule(scheduleID, true);
		}
		if(schedule==null) {
			return new ArrayList<ScheduleDTO>();
		}
		/*Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleDTO>();
		}*/
		Integer teamId = schedule.getGuestTeamID();
		if (StringUtils.equals(schedule.getTurn(), "1")) {
			teamId = schedule.getHomeTeamID();
		}
		List<Schedule> schedules = Schedule.findPreSchedules(teamId, schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	public Collection<ScheduleDTO> getAfterHomeSchedules(int scheduleID, Schedule schedule) {
		if (schedule==null) {
			schedule = Schedule.findSchedule(scheduleID, true);
		}
		if(schedule==null) {
			return new ArrayList<ScheduleDTO>();
		}
		/*Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleDTO>();
		}*/
		Integer teamId = schedule.getHomeTeamID();
		if (StringUtils.equals(schedule.getTurn(), "1")) {
			teamId = schedule.getGuestTeamID();
		}
		List<Schedule> schedules = Schedule.findAfterSchedules(teamId, schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	public Collection<ScheduleDTO> getAfterGuestSchedules(int scheduleID, Schedule schedule) {
		if (schedule==null) {
			schedule = Schedule.findSchedule(scheduleID, true);
		}
		if(schedule==null) {
			return new ArrayList<ScheduleDTO>();
		}
		/*Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleDTO>();
		}*/
		Integer teamId = schedule.getGuestTeamID();
		if (StringUtils.equals(schedule.getTurn(), "1")) {
			teamId = schedule.getHomeTeamID();
		}
		List<Schedule> schedules = Schedule.findAfterSchedules(teamId, schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	public Collection<ScheduleDTO> getPreClashSchedules(int scheduleID, Schedule schedule) {
		if (schedule==null) {
			schedule = Schedule.findSchedule(scheduleID, true);
		}
		if(schedule==null) {
			return new ArrayList<ScheduleDTO>();
		}
		/*Schedule schedule = Schedule.findSchedule(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleDTO>();
		}*/
		List<Schedule> schedules = Schedule.findPreClashSchedules(schedule.getHomeTeamID(), schedule.getGuestTeamID(), schedule.getMatchTime());
		return buildDTOS(schedules);
	}

	public List<ScheduleDTO> buildDTOS(List<Schedule> schedules) {
		List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
		for(Schedule s : schedules) {
			ScheduleDTO dto = buildDTO(s);
			dtos.add(dto);
		}
		return dtos;
	}
	
	public ScheduleDTO buildDTO(Schedule schedule) {
		ScheduleDTO dto = new ScheduleDTO();
		try {
			Sclass sclass = Sclass.findSclass(schedule.getSclassID());
			BeanUtilsEx.copyProperties(dto, schedule);
			dto.setSclassName(sclass.getName_J());
			dto.setSclassName_j(sclass.getName_JS());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
}
