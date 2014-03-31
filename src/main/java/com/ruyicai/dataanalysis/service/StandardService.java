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
	
	public Map<String, StandardDto> getUsualStandards(String companyId) {
		String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", companyId);
		Map<String, StandardDto> map = cacheService.get(key);
		if (map==null) {
			map = new HashMap<String, StandardDto>();
			//查询betState=1在售的赛事
			List<Integer> scheduleIds = Schedule.findSaleIds();
			if (scheduleIds!=null&&scheduleIds.size()>0) {
				for (Integer scheduleId : scheduleIds) {
					Schedule schedule = Schedule.findSchedule(scheduleId);
					if (StringUtils.isBlank(schedule.getEvent())) {
						continue;
					}
					StandardDto dto = null;
					if (StringUtils.equals(companyId, "avg")) { //平均欧赔
						dto = getAvgStandardDto(schedule);
					} else {
						dto = getStandardDtoByCompanyId(scheduleId, Integer.parseInt(companyId));
					}
					map.put(schedule.getEvent(), dto);
				}
			}
			if (map.size()>0) {
				cacheService.set(key, map);
			}
		}
		return map;
	}
	
	private StandardDto getAvgStandardDto(Schedule schedule) {
		StandardDto avgDto = new StandardDto();
		avgDto.setHomeWin(schedule.getAvgH());
		avgDto.setStandoff(schedule.getAvgS());
		avgDto.setGuestWin(schedule.getAvgG());
		return avgDto;
	}
	
	private StandardDto getStandardDtoByCompanyId(Integer scheduleId, Integer companyId) {
		Standard standard = Standard.findByScheduleIdCompanyId(scheduleId, companyId);
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
