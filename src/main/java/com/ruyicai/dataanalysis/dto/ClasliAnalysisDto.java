package com.ruyicai.dataanalysis.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class ClasliAnalysisDto {

	private ScheduleDTO schedule = new ScheduleDTO();
	
	private BetRatioDto betRatio = new BetRatioDto();
	
	private BetNumDto betNum = new BetNumDto();
	
	private Collection<ScheduleDTO> preClashSchedules = new ArrayList<ScheduleDTO>();
	
	private Collection<RankingDTO> rankings = new ArrayList<RankingDTO>();
	
	private LetgoalDto letgoal = new LetgoalDto();
	
}
