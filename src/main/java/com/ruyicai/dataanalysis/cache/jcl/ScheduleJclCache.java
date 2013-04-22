package com.ruyicai.dataanalysis.cache.jcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.jcl.ScheduleJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class ScheduleJclCache {

	@Autowired
	private CacheService cacheService;

	public ScheduleJcl getScheduleJcl(Integer scheduleId) {
		String id = StringUtil.join("_", "dadaanalysis", "ScheduleJcl", String.valueOf(scheduleId));
		String value = cacheService.get(id);
		ScheduleJcl scheduleJcl = null;
		if (StringUtil.isEmpty(value)) {
			scheduleJcl = ScheduleJcl.findById(scheduleId);
			if (null != scheduleJcl) {
				cacheService.set(id, 0, scheduleJcl.toJson());
			}
		} else {
			scheduleJcl = ScheduleJcl.fromJsonToScheduleJcl(value);
		}
		return scheduleJcl;
	}

	public void setToMemcache(ScheduleJcl scheduleJcl) {
		String id = StringUtil.join("_", "dadaanalysis", "ScheduleJcl", String.valueOf(scheduleJcl.getScheduleId()));
		cacheService.set(id, 0, scheduleJcl.toJson());
	}
}
