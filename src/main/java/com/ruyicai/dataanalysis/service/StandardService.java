package com.ruyicai.dataanalysis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.service.dto.StandardDto;

@Service
public class StandardService {

	public Map<String, List<StandardDto>> getUsualStandards() {
		Map<String, List<StandardDto>> map = new HashMap<String, List<StandardDto>>();
		//查询betState=1在售的赛事
		List<Integer> scheduleIds = Schedule.findSaleIds();
		if (scheduleIds!=null&&scheduleIds.size()>0) {
			for (Integer scheduleId : scheduleIds) {
				List<StandardDto> list = new ArrayList<StandardDto>();
				Schedule schedule = Schedule.findSchedule(scheduleId);
				//平均欧赔
				StandardDto avgDto = new StandardDto();
				avgDto.setCompanyId("avg");
				avgDto.setHomeWin(schedule.getAvgH());
				avgDto.setStandoff(schedule.getAvgS());
				avgDto.setGuestWin(schedule.getAvgG());
				list.add(avgDto);
				//威廉希尔
				getStandardDtoByCompanyId(scheduleId, 115, list);
				//立博
				getStandardDtoByCompanyId(scheduleId, 82, list);
				//bwin
				getStandardDtoByCompanyId(scheduleId, 255, list);
				//澳门
				getStandardDtoByCompanyId(scheduleId, 80, list);
				//bet365
				getStandardDtoByCompanyId(scheduleId, 281, list);
			}
		}
		return map;
	}
	
	private void getStandardDtoByCompanyId(Integer scheduleId, Integer companyId, List<StandardDto> list) {
		Standard wlxeStandard = Standard.findByScheduleIdCompanyId(scheduleId, companyId);
		StandardDto wlxeDto = new StandardDto();
		wlxeDto.setCompanyId(String.valueOf(companyId));
		wlxeDto.setHomeWin(wlxeStandard.getHomeWin());
		wlxeDto.setStandoff(wlxeStandard.getStandoff());
		wlxeDto.setGuestWin(wlxeStandard.getGuestWin());
		list.add(wlxeDto);
	}
	
}
