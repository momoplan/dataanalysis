package com.ruyicai.dataanalysis.cache.lq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.lq.TeamJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class TeamJclCache {

	@Autowired
	private CacheService cacheService;
	
	public TeamJcl getTeamJcl(Integer teamId) {
		String id = StringUtil.join("_", "dadaanalysis", "TeamJcl", String.valueOf(teamId));
		String value = cacheService.get(id);
		TeamJcl teamJcl = null;
		if(StringUtil.isEmpty(value)) {
			teamJcl = TeamJcl.findById(teamId);
			if(null != teamJcl) {
				cacheService.set(id, 0, teamJcl.toJson());
			}
		} else {
			teamJcl = TeamJcl.fromJsonToTeamJcl(value);
		}
		return teamJcl;
	}
	
	public void setToMemcache(TeamJcl teamJcl) {
		String id = StringUtil.join("_", "dadaanalysis", "TeamJcl", String.valueOf(teamJcl.getTeamId()));
		cacheService.set(id, 0, teamJcl.toJson());
	}
}
