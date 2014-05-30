package com.ruyicai.dataanalysis.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.domain.Team;
import com.ruyicai.dataanalysis.exception.RuyicaiException;

@Service
public class TeamService {

	/**
	 * 支持球队
	 * @param userno
	 * @param teamid
	 */
	@Transactional
	public void support(String teamid) {
		if (StringUtils.isBlank(teamid)) {
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		Team team = Team.findTeam(Integer.parseInt(teamid));
		if (team==null) {
			throw new RuyicaiException(ErrorCode.teamNotExist);
		}
		Integer support = team.getSupport()==null ? 0 : team.getSupport();
		team.setSupport(support+1);
		team.merge();
	}
	
}
