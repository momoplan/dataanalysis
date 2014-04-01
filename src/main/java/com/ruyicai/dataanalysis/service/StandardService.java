package com.ruyicai.dataanalysis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.dto.KaiLiDto;
import com.ruyicai.dataanalysis.dto.ProbabilityDto;
import com.ruyicai.dataanalysis.dto.StandardDto;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.zq.CalcUtil;

@Service
public class StandardService {

	@Autowired
	private CacheService cacheService;
	
	public Map<String, StandardDto> getUsualStandard(String day, String companyId) {
		Map<String, StandardDto> resultMap = new HashMap<String, StandardDto>();
		try {
			String[] days = StringUtils.splitByWholeSeparator(day, ",");
			for (String dayStr : days) {
				if (StringUtils.isNotBlank(dayStr)) {
					Map<String, StandardDto> map = getUsualStandardByDayCompanyId(dayStr, companyId);
					if (map!=null&&map.size()>0) {
						resultMap.putAll(map);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	private Map<String, StandardDto> getUsualStandardByDayCompanyId(String day, String companyId) {
		try {
			String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", day, companyId);
			Map<String, StandardDto> map = cacheService.get(key);
			if (map==null) {
				map = new HashMap<String, StandardDto>();
				List<Schedule> list = Schedule.findByEventAndDay(day);
				if (list!=null&&list.size()>0) {
					for (Schedule schedule : list) {
						if (StringUtils.isBlank(schedule.getEvent())) {
							continue;
						}
						StandardDto dto = null;
						if (StringUtils.equals(companyId, "avg")) { //平均欧赔
							dto = getAvgStandardDto(schedule);
						} else {
							dto = getStandardDtoByCompanyId(schedule.getScheduleID(), Integer.parseInt(companyId));
						}
						if (dto!=null) {
							map.put(schedule.getEvent(), dto);
						}
					}
				}
				if (map.size()>0) {
					cacheService.set(key, map);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StandardDto getAvgStandardDto(Schedule schedule) {
		StandardDto avgDto = new StandardDto();
		avgDto.setScheduleId(String.valueOf(schedule.getScheduleID()));
		avgDto.setHomeWin(schedule.getAvgH());
		avgDto.setStandoff(schedule.getAvgS());
		avgDto.setGuestWin(schedule.getAvgG());
		return avgDto;
	}
	
	public StandardDto getStandardDtoByCompanyId(Integer scheduleId, Integer companyId) {
		Standard standard = Standard.findStandard(scheduleId, companyId);
		if (standard==null) {
			return null;
		}
		Double homeWin = standard.getHomeWin()==null ? standard.getFirstHomeWin() : standard.getHomeWin();
		Double standoff = standard.getStandoff()==null ? standard.getFirstStandoff() : standard.getStandoff();
		Double guestWin = standard.getGuestWin()==null ? standard.getFirstGuestWin() : standard.getGuestWin();
		
		StandardDto dto = new StandardDto();
		dto.setScheduleId(String.valueOf(scheduleId));
		dto.setHomeWin(homeWin);
		dto.setStandoff(standoff);
		dto.setGuestWin(guestWin);
		return dto;
	}
	
	public Map<String, ProbabilityDto> getUsualProbability(String day, String companyId) {
		Map<String, ProbabilityDto> resultMap = new HashMap<String, ProbabilityDto>();
		try {
			String[] days = StringUtils.splitByWholeSeparator(day, ",");
			for (String dayStr : days) {
				Map<String, StandardDto> smap = getUsualStandardByDayCompanyId(dayStr, companyId);
				if (smap!=null&&smap.size()>0) {
					for(Map.Entry<String, StandardDto> entry : smap.entrySet()) {
						String event = entry.getKey();
						StandardDto sdto = entry.getValue();
						
						Double homeWinLu = CalcUtil.probability_H(sdto.getHomeWin(), sdto.getStandoff(), sdto.getGuestWin());
						Double standoffLu = CalcUtil.probability_S(sdto.getHomeWin(), sdto.getStandoff(), sdto.getGuestWin());
						Double guestWinLu = CalcUtil.probability_G(sdto.getHomeWin(), sdto.getStandoff(), sdto.getGuestWin());
						
						ProbabilityDto dto = new ProbabilityDto();
						dto.setHomeWinLu(homeWinLu);
						dto.setStandoffLu(standoffLu);
						dto.setGuestWinLu(guestWinLu);
						resultMap.put(event, dto);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public Map<String, KaiLiDto> getUsualKaiLi(String day, String companyId) {
		Map<String, KaiLiDto> resultMap = new HashMap<String, KaiLiDto>();
		try {
			String[] days = StringUtils.splitByWholeSeparator(day, ",");
			for (String dayStr : days) {
				Map<String, StandardDto> smap = getUsualStandardByDayCompanyId(dayStr, companyId);
				if (smap!=null&&smap.size()>0) {
					for(Map.Entry<String, StandardDto> entry : smap.entrySet()) {
						String event = entry.getKey();
						StandardDto sdto = entry.getValue();
						
						Schedule schedule = Schedule.findScheduleWOBuild(Integer.parseInt(sdto.getScheduleId()));
						Double k_h = CalcUtil.k_h(sdto.getHomeWin(), schedule);
						Double k_s = CalcUtil.k_s(sdto.getStandoff(), schedule);
						Double k_g = CalcUtil.k_g(sdto.getGuestWin(), schedule);
						
						KaiLiDto dto = new KaiLiDto();
						dto.setK_h(k_h);
						dto.setK_s(k_s);
						dto.setK_g(k_g);
						resultMap.put(event, dto);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
}
