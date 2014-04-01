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
import com.ruyicai.dataanalysis.dto.StandardDto;
import com.ruyicai.dataanalysis.util.StringUtil;

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
		StandardDto dto = new StandardDto();
		dto.setHomeWin(standard.getHomeWin());
		dto.setStandoff(standard.getStandoff());
		dto.setGuestWin(standard.getGuestWin());
		return dto;
	}
	
}
