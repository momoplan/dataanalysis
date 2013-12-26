package com.ruyicai.dataanalysis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class ScheduleCache {

	@Autowired
	private CacheService cacheService;
	
	public Schedule getSchedule(Integer scheduleID) {
		String value = cacheService.get(StringUtil.join("_", "dadaanalysis", "Schedule", String.valueOf(scheduleID)));
		Schedule schedule = null;
		if(StringUtil.isEmpty(value)) {
			schedule = Schedule.findById(scheduleID);
			if(null != schedule) {
					cacheService.set(StringUtil.join("_", "dadaanalysis", "Schedule", String.valueOf(scheduleID)), 0, schedule.toJson());
			}
		} else {
			schedule = Schedule.fromJsonToSchedule(value);
		}
		return schedule;
	}
	
	public void setToMemcache(Schedule schedule) {
		cacheService.set(StringUtil.join("_", "dadaanalysis", "Schedule", String.valueOf(schedule.getScheduleID())), 0, schedule.toJson());
	}
	
	public void deleteFromMemcache(Schedule schedule) {
		cacheService.delete(StringUtil.join("_", "dadaanalysis", "Schedule", String.valueOf(schedule.getScheduleID())));
	}
	
}
