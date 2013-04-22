package com.ruyicai.dataanalysis.service.dto.jcl;

import java.util.Collection;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import com.ruyicai.dataanalysis.domain.jcl.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.jcl.StandardJcl;
import com.ruyicai.dataanalysis.domain.jcl.TotalScoreJcl;
import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class InfoJclDTO {

	private ScheduleJclDTO schedule;
	
	private Collection<LetGoalJcl> letGoals;
	
	private Collection<TotalScoreJcl> totalScores;
	
	private Collection<StandardJcl> standards;
	
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
