package com.ruyicai.dataanalysis.dto;

import java.util.Collection;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class ClasliAnalysisDto {

	private ScheduleDTO schedule;
	
	private BetRatioDto betRatio;
	
	private Collection<ScheduleDTO> preClashSchedules;
	
	private Collection<RankingDTO> rankings;
	
}
