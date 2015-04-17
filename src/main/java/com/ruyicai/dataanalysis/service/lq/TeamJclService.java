package com.ruyicai.dataanalysis.service.lq;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.domain.lq.TeamJcl;
import com.ruyicai.dataanalysis.exception.RuyicaiException;

/**
 * @Description: 球队支持
 * 
 * @author chenchuang   
 * @date 2015年2月15日下午4:17:54
 * @version V1.0   
 *
 */
@Service
public class TeamJclService {

	@Transactional
	public void support(String teamid) {
		if (StringUtils.isBlank(teamid)) {
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		TeamJcl team = TeamJcl.findTeamJcl(Integer.parseInt(teamid));
		if (team==null) {
			throw new RuyicaiException(ErrorCode.teamNotExist);
		}
		Integer support = team.getSupport()==null ? 50 : team.getSupport();
		team.setSupport(support+1);
		team.merge();
	}
	
}




