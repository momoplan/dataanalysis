package com.ruyicai.dataanalysis.service.lq;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.TotalScoreJcl;
import com.ruyicai.dataanalysis.dto.lq.LetgoalJclDto;
import com.ruyicai.dataanalysis.dto.lq.LetgoalsJclDto;
import com.ruyicai.dataanalysis.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.util.zq.CalcUtil;

/**
 * @Description: 竞彩篮球亚盘
 * 
 * @author chenchuang   
 * @date 2015年3月13日下午3:17:11
 * @version V1.0   
 *
 */
@Service
public class LetgoalJclService {
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private GlobalInfoJclService globalInfoJclService;

	public LetgoalJclDto getLetgoalJclDtoByCompanyId(int scheduleId, int companyId) {
		LetGoalJcl letGoal = LetGoalJcl.findLetGoalJcl(scheduleId, companyId);
		if (letGoal==null) {
			return null;
		}
		LetgoalJclDto dto = new LetgoalJclDto();
		dto.setGoal(letGoal.getGoal());
		dto.setUpOdds(letGoal.getUpOdds());
		dto.setDownOdds(letGoal.getDownOdds());
		dto.setGoalName(CalcUtil.handicap(letGoal.getGoal()));
		return dto;
	}
	
	public LetgoalsJclDto findByEvent(String event) {
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		if (schedule==null) {
			return null;
		}
		ScheduleJclDTO scheduleDTO = analysisJclService.buildDTO(schedule, false,false);
		GlobalCacheJcl letGoal = globalInfoJclService.getLetGoal(schedule);
		Collection<LetGoalJcl> letGoals = LetGoalJcl.fromJsonArrayToLetGoalJcls(letGoal.getValue());
		
		LetgoalsJclDto resultDto = new LetgoalsJclDto();
		resultDto.setSchedule(scheduleDTO);
		resultDto.setLetGoals(letGoals);
		return resultDto;
	}
	
	public LetgoalsJclDto getTotalScore(String event) {
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		if (schedule==null) {
			return null;
		}
		ScheduleJclDTO scheduleDTO = analysisJclService.buildDTO(schedule, false,false);
		GlobalCacheJcl totalScore = globalInfoJclService.getTotalScore(schedule);
		Collection<TotalScoreJcl> totalScores = TotalScoreJcl.fromJsonArrayToTotalScoreJcls(totalScore.getValue());
		
		LetgoalsJclDto resultDto = new LetgoalsJclDto();
		resultDto.setSchedule(scheduleDTO);
		resultDto.setTotalScores(totalScores);
		return resultDto;
	}
	
}




