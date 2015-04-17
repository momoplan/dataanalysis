package com.ruyicai.dataanalysis.dto.lq;

import java.util.Collection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.dto.BetRatioDto;

import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class AnalysisJclDto {

	private ScheduleJclDTO schedule;
	
	private BetRatioDto betRatio;
	
	private Collection<ScheduleJclDTO> homePreSchedules;
	
	private Collection<ScheduleJclDTO> guestPreSchedules;
	
	private Collection<ScheduleJclDTO> homeAfterSchedules;
	
	private Collection<ScheduleJclDTO> guestAfterSchedules;
	
	private Collection<ScheduleJclDTO> preClashSchedules;
	
	private Collection<RankingJclDTO> rankings;
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
	
}




