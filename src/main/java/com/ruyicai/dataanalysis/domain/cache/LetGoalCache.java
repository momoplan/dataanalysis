package com.ruyicai.dataanalysis.domain.cache;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class LetGoalCache {

	@Autowired
	private CacheService cacheService;
	
	public LetGoal getLetGoal(Integer scheduleId, Integer companyId) {
		String key = StringUtil.join("_", "dadaanalysis", "LetGoal", String.valueOf(scheduleId), String.valueOf(companyId));
		String value = cacheService.get(key);
		LetGoal letGoal = null;
		if (StringUtils.isBlank(value)) {
			letGoal = LetGoal.findByScheduleIdCompanyId(scheduleId, companyId);
			if (letGoal!=null) {
				cacheService.set(key, 24*60*60, letGoal.toJson());
			}
		} else {
			letGoal = LetGoal.fromJsonToLetGoal(value);
		}
		return letGoal;
	}
	
	public void setToMemcache(LetGoal letGoal) {
		String key = StringUtil.join("_", "dadaanalysis", "LetGoal", String.valueOf(letGoal.getScheduleID()), String.valueOf(letGoal.getCompanyID()));
		cacheService.set(key, 24*60*60, letGoal.toJson());
	}
	
}
