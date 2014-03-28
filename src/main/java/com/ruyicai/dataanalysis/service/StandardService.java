package com.ruyicai.dataanalysis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.service.dto.StandardDto;

@Service
public class StandardService {

	public Map<String, StandardDto> getUsualStandards(String companyId) {
		Map<String, StandardDto> map = new HashMap<String, StandardDto>();
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
				//威廉希尔
				/*getStandardDtoByCompanyId(scheduleId, 115, list);
				//立博
				getStandardDtoByCompanyId(scheduleId, 82, list);
				//bwin
				getStandardDtoByCompanyId(scheduleId, 255, list);
				//澳门
				getStandardDtoByCompanyId(scheduleId, 80, list);
				//bet365
				getStandardDtoByCompanyId(scheduleId, 281, list);
				*/
			}
		}
		return map;
	}
	
	private StandardDto getAvgStandardDto(Schedule schedule) {
		StandardDto avgDto = new StandardDto();
		//avgDto.setCompanyId("avg");
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
		//dto.setCompanyId(String.valueOf(companyId));
		dto.setHomeWin(standard.getHomeWin());
		dto.setStandoff(standard.getStandoff());
		dto.setGuestWin(standard.getGuestWin());
		return dto;
	}
	
}
