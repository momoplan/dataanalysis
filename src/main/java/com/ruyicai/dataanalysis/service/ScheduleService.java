package com.ruyicai.dataanalysis.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.MatchState;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.TechnicCount;
import com.ruyicai.dataanalysis.dto.ClasliAnalysisDto;
import com.ruyicai.dataanalysis.dto.RankingDTO;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;
import com.ruyicai.dataanalysis.dto.TechnicCountDto;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;

@Service
public class ScheduleService {

	@Autowired
	private AnalysisService analysisService;
	
	@Autowired
	private GlobalInfoService infoService;
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * 查询即时比分
	 * @param state
	 * @return
	 */
	public Map<String, List<ScheduleDTO>> findInstantScores(int state) {
		Map<String, List<ScheduleDTO>> resultMap = new HashMap<String, List<ScheduleDTO>>();
		if (state==1) { //未开赛
			List<String> activedays = commonService.getActivedays("1");
			if (activedays==null||activedays.size()<=0) {
				return null;
			}
			for (String activeday : activedays) {
				List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
				List<Schedule> schedules = Schedule.findByEventAndDay(activeday);
				for (Schedule schedule : schedules) {
					Integer matchState = schedule.getMatchState();
					if (matchState==null) {
						continue;
					}
					if (matchState!=MatchState.WEIKAI.value&&matchState!=MatchState.DAIDING.value
							&&matchState!=MatchState.TUICHI.value) {
						continue;
					}
					ScheduleDTO dto = analysisService.buildDTO(schedule);
					dtos.add(dto);
				}
				if (dtos!=null&&dtos.size()>0) {
					sortScheduleDtoList(dtos); //排序
					resultMap.put(activeday, dtos);
				}
			}
		} else if (state==2) { //进行中
			List<Schedule> schedules = Schedule.findProcessingMatches();
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
				ScheduleDTO dto = analysisService.buildDTO(schedule);
				dtos.add(dto);
				if (dtos!=null&&dtos.size()>0) {
					resultMap.put(day, dtos);
				}
			}
		} else if (state==3) { //完场
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			List<String> days = new ArrayList<String>();
			days.add(sdf.format(DateUtil.getPreDate(0))); //今天
			days.add(sdf.format(DateUtil.getPreDate(1))); //昨天
			for (String day : days) {
				List<ScheduleDTO> dtos = new ArrayList<ScheduleDTO>();
				List<Schedule> schedules = Schedule.findByEventAndDay(day);
				for (Schedule schedule : schedules) {
					Integer matchState = schedule.getMatchState();
					if (matchState==null) {
						continue;
					}
					if (matchState!=MatchState.YAOZHAN.value&&matchState!=MatchState.WANCHANG.value
							&&matchState!=MatchState.QUXIAO.value) {
						continue;
					}
					ScheduleDTO dto = analysisService.buildDTO(schedule);
					dtos.add(dto);
				}
				if (dtos!=null&&dtos.size()>0) {
					sortScheduleDtoList(dtos); //排序
					resultMap.put(day, dtos);
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 排序ScheduleDTO数组
	 * @param list
	 */
	private void sortScheduleDtoList(List<ScheduleDTO> list) {
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
	
	/*public static void main(String[] args) {
		String o1 = "1_20140603_2_051";
		String o2 = "1_20140602_2_053";
		System.out.println(o1.compareTo(o2));
	}*/
	
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
		dto.setSchedule(analysisService.buildDTO(schedule));
		dto.setTechnicCount(technicCount);
		return dto;
	}
	
	public ClasliAnalysisDto findClasliAnalysis(String event) {
		Schedule schedule = Schedule.findByEvent(event, true);
		if(null == schedule) {
			return null;
		}
		ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule);
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
			List<Schedule> schedules = Schedule.findByEventAndDay(activeday);
			for (Schedule schedule : schedules) {
				ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule);
				resultList.add(scheduleDTO);
			}
		}
		return resultList;
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
			ScheduleDTO scheduleDTO = analysisService.buildDTO(schedule);
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
		/*Map<Integer, List<ScheduleDTO>> map = new HashMap<Integer, List<ScheduleDTO>>();
		if (processingList!=null&&processingList.size()>0) {
			map.put(1, processingList);
		}
		if (wanchangList!=null&&wanchangList.size()>0) {
			map.put(2, wanchangList);
		}
		if (weikaiList!=null&&weikaiList.size()>0) {
			map.put(3, weikaiList);
		}
		Tools.sortMapByKey(map, false);*/
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
