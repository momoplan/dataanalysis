package com.ruyicai.dataanalysis.dto;

import java.util.Collection;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class AnalysisDto {

	private ScheduleDTO schedule;
	
	private Collection<ScheduleDTO> homePreSchedules;
	
	private Collection<ScheduleDTO> guestPreSchedules;
	
	private Collection<ScheduleDTO> homeAfterSchedules;
	
	private Collection<ScheduleDTO> guestAfterSchedules;
	
	private Collection<ScheduleDTO> preClashSchedules;
	
	private Collection<RankingDTO> rankings;
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
}
