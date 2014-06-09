package com.ruyicai.dataanalysis.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.TechnicCount;
import com.ruyicai.dataanalysis.dto.ClasliAnalysisDto;
import com.ruyicai.dataanalysis.dto.RankingDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.dto.TechnicCountDto;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;

@Service
public class ScheduleService {

	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private GlobalInfoService infoService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * 查询即时比分
	 * @param state
	 * @return
	 */
	public Map<String, List<ScheduleDTO>> findInstantScores(int state) {
		Map<String, List<ScheduleDTO>> resultMap = new LinkedHashMap<String, List<ScheduleDTO>>();
		if (state==1) { //未开赛
			List<String> activedays = commonService.getActivedays("1");
			if (activedays==null||activedays.size()<=0) {
				return null;
			}
			for (String activeday : activedays) {
				List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
				List<Schedule> schedules = getSchedulesByEventAndDay(activeday);
				if (schedules==null||schedules.size()<=0) {
					continue;
				}
				for (Schedule schedule : schedules) {
					Integer matchState = schedule.getMatchState();
					if (matchState==null) {
						continue;
					}
					if (matchState!=MatchState.WEIKAI.value&&matchState!=MatchState.DAIDING.value
							&&matchState!=MatchState.TUICHI.value) {
						continue;
					}
					ScheduleDTO dto = analysisService.buildDTO(schedule, true);
					dtos.add(dto);
				}
				if (dtos!=null&&dtos.size()>0) {
					resultMap.put(activeday, dtos);
				}
			}
		} else if (state==2) { //进行中
			List<Schedule> schedules = getProcessingSchedulesFromCache();
			if (schedules==null||schedules.size()<=0) {
				return null;
			}
			for (Schedule schedule : schedules) {
				String event = schedule.getEvent();
				if (StringUtils.isBlank(event)) {
					continue;
				}
				String day = JingCaiUtil.getDayByEvent(event);
				if (StringUtils.isBlank(day)) {
					continue;
				}
				List<ScheduleDTO> dtos = resultMap.get(day);
				if (dtos==null) {
					dtos = new ArrayList<ScheduleDTO>();
				}
				ScheduleDTO dto = analysisService.buildDTO(schedule, true);
				dtos.add(dto);
				if (dtos!=null&&dtos.size()>0) {
					resultMap.put(day, dtos);
				}
			}
		} else if (state==3) { //完场
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			List<String> days = new ArrayList<String>();
			days.add(sdf.format(DateUtil.getPreDate(2))); //前天
			days.add(sdf.format(DateUtil.getPreDate(1))); //昨天
			days.add(sdf.format(DateUtil.getPreDate(0))); //今天
			for (String day : days) {
				List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
				List<Schedule> schedules = getSchedulesByEventAndDay(day);
				if (schedules==null||schedules.size()<=0) {
					continue;
				}
				for (Schedule schedule : schedules) {
					Integer matchState = schedule.getMatchState();
					if (matchState==null) {
						continue;
					}
					if (matchState!=MatchState.YAOZHAN.value&&matchState!=MatchState.WANCHANG.value
							&&matchState!=MatchState.QUXIAO.value) {
						continue;
					}
					ScheduleDTO dto = analysisService.buildDTO(schedule, true);
					dtos.add(dto);
				}
				if (dtos!=null&&dtos.size()>0) {
					resultMap.put(day, dtos);
				}
			}
		}
		//排序
		if (resultMap!=null&&resultMap.size()>0) {
			for(Map.Entry<String, List<ScheduleDTO>> entry : resultMap.entrySet()) {
				String key = entry.getKey();
				List<ScheduleDTO> value = entry.getValue();
				sortScheduleDtoList(value); //排序
				resultMap.put(key, value);
			}
		}
		return resultMap;
	}
	
	/*public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", 2);
		map.put("1", 3);
		System.out.println(map.get("1"));
	}*/
	
	/**
	 * 排序ScheduleDTO数组
	 * @param list
	 */
	private static void sortScheduleDtoList(List<ScheduleDTO> list) {
		Collections.sort(list, new Comparator<ScheduleDTO>() {
			@Override
			public int compare(ScheduleDTO o1, ScheduleDTO o2) {
				if (o1.getEvent().compareTo(o2.getEvent())<0) {
					return -1;
				}
				return 1;
			}
		});
	}
	
	private List<Schedule> getProcessingSchedulesFromCache() {
		try {
			String key = StringUtil.join("_", "dadaanalysis", "ProcessingSchedules");
			List<Schedule> value = cacheService.get(key);
			if (value==null) {
				value = Schedule.findProcessingMatches();
				if (value!=null) {
					cacheService.set(key, 72*60*60, value);
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public TechnicCountDto findTechnicCount(String event) {
		Schedule schedule = Schedule.findByEvent(event, true);
		if(null == schedule) {
			return null;
		}
		TechnicCount technicCount = TechnicCount.findTechnicCount(schedule.getScheduleID());
		if (technicCount==null) {
			return null;
		}
		TechnicCountDto dto = new TechnicCountDto();
		dto.setSchedule(analysisService.buildDTO(schedule, false));
		dto.setTechnicCount(technicCount);
		return dto;
	}
	
	public ClasliAnalysisDto findClasliAnalysis(String event) {
		Schedule schedule = Schedule.findByEvent(event, true);
		if(null == schedule) {
			return null;
		}
		ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule, true);
		int scheduleId = schedule.getScheduleID();
		//历史交锋
		Collection<ScheduleDTO> preClashSchedules = analysisService.getPreClashSchedules(scheduleId, schedule);
		//联赛排名
		Collection<RankingDTO> rankingDtos = infoService.getRankingDtos(scheduleId, schedule.getSclassID());
		ClasliAnalysisDto dto = new ClasliAnalysisDto();
		dto.setSchedule(scheduleDTO);
		dto.setBetRatio(infoService.getBetRatioDto(event));
		dto.setBetNum(infoService.getBetNumDto(event));
		dto.setPreClashSchedules(preClashSchedules);
		dto.setRankings(rankingDtos);
		return dto;
	}
	
	/**
	 * 查询对阵里的赛事信息
	 * @return
	 */
	public List<ScheduleDTO> findClasliSchedules() {
		List<String> activedays = commonService.getActivedays("1");
		if (activedays==null||activedays.size()<=0) {
			return null;
		}
		List<ScheduleDTO> resultList = new ArrayList<ScheduleDTO>();
		for (String activeday : activedays) {
			List<Schedule> schedules = getSchedulesByEventAndDay(activeday);
			if (schedules==null||schedules.size()<=0) {
				continue;
			}
			for (Schedule schedule : schedules) {
				ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule, true);
				resultList.add(scheduleDTO);
			}
		}
		return resultList;
	}
	
	private List<Schedule> getSchedulesByEventAndDay(String day) {
		try {
			String key = StringUtil.join("_", "dadaanalysis", "SchedulesByEventAndDay", day);
			List<Schedule> value = cacheService.get(key);
			if (value==null) {
				value = Schedule.findByEventAndDay(day);
				if (value!=null&&value.size()>0) {
					cacheService.set(key, 72*60*60, value);
				}
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ScheduleDTO> findScheduleByEvents(String events) {
		if (StringUtils.isBlank(events)) {
			return null;
		}
		String[] separator = StringUtils.splitByWholeSeparator(events, ",");
		if (separator==null||separator.length<=0) {
			return null;
		}
		List<ScheduleDTO> processingList = new ArrayList<ScheduleDTO>();
		List<ScheduleDTO> wanchangList = new ArrayList<ScheduleDTO>();
		List<ScheduleDTO> weikaiList = new ArrayList<ScheduleDTO>();
		for (String event : separator) {
			Schedule schedule = Schedule.findByEvent(event, true);
			if(schedule==null) {
				continue;
			}
			ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule, true);
			if (scheduleDTO==null) {
				continue;
			}
			Integer matchState = scheduleDTO.getMatchState();
			if (matchState==null) {
				continue;
			}
			if (matchState==MatchState.SHANGBANCHANG.value||matchState==MatchState.ZHONGCHANG.value
					||matchState==MatchState.XIABANCHANG.value||matchState==MatchState.ZHONGDUAN.value) { //进行中
				processingList.add(scheduleDTO);
			}
			if (matchState==MatchState.YAOZHAN.value||matchState==MatchState.WANCHANG.value
					||matchState==MatchState.QUXIAO.value) { //完场
				wanchangList.add(scheduleDTO);
			}
			if (matchState==MatchState.WEIKAI.value||matchState==MatchState.DAIDING.value
					||matchState==MatchState.TUICHI.value) { //未开赛
				weikaiList.add(scheduleDTO);
			}
		}
		List<ScheduleDTO> resultList = new ArrayList<ScheduleDTO>();
		resultList.addAll(processingList);
		resultList.addAll(wanchangList);
		resultList.addAll(weikaiList);
		if (resultList==null||resultList.size()<=0) {
			return null;
		}
		return resultList;
	}
	
	/*public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		for (String string : list) {
			System.out.println(string);
		}
	}*/
	
}
