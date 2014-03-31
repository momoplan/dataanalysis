package com.ruyicai.dataanalysis.dto;

import java.util.Collection;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import com.ruyicai.dataanalysis.domain.LetGoal;
import com.ruyicai.dataanalysis.domain.Standard;
import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class InfoDTO {

	private ScheduleDTO schedule;
	
	private Collection<LetGoal> letGoals;
	
	private Collection<Standard> standards;
	
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
