package com.ruyicai.dataanalysis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Team;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class TeamCache {

	@Autowired
	private CacheService cacheService;
	
	public Team getTeam(Integer teamID) {
		String value = cacheService.get(StringUtil.join("_", "dadaanalysis", "Team", String.valueOf(teamID)));
		Team team = null;
		if(StringUtil.isEmpty(value)) {
			team = Team.findById(teamID);
			if(null != team) {
					cacheService.set(StringUtil.join("_", "dadaanalysis", "Team", String.valueOf(teamID)), 0, team.toJson());
			}
		} else {
			team = Team.fromJsonToTeam(value);
		}
		return team;
	}
	
	public void setToMemcache(Team team) {
			cacheService.set(StringUtil.join("_", "dadaanalysis", "Team", String.valueOf(team.getTeamID())), 0, team.toJson());
	}
}
