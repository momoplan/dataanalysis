package com.ruyicai.dataanalysis.service.lq;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.StandardJcl;
import com.ruyicai.dataanalysis.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.dto.lq.StandardsJclDto;

@Service
public class StandardJclService {
	
	@Autowired
	private AnalysisJclService analysisJclService;
	
	@Autowired
	private GlobalInfoJclService globalInfoJclService;

	public StandardsJclDto findByEvent(String event) {
		ScheduleJcl schedule = ScheduleJcl.findByEvent(event, true);
		if (schedule==null) {
			return null;
		}
		ScheduleJclDTO scheduleDTO = analysisJclService.buildDTO(schedule, false,false);
		GlobalCacheJcl standard = globalInfoJclService.getStandard(schedule);
		Collection<StandardJcl> standards = StandardJcl.fromJsonArrayToStandardJcls(standard.getValue());
		
		StandardsJclDto resultDto = new StandardsJclDto();
		resultDto.setSchedule(scheduleDTO);
		resultDto.setStandards(standards);
		return resultDto;
	}
	
}




