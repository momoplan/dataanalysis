package com.ruyicai.dataanalysis.dto.lq;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.domain.lq.LetGoalJcl;
import com.ruyicai.dataanalysis.domain.lq.TotalScoreJcl;

@RooJavaBean
@RooJson
public class LetgoalsJclDto implements Serializable  {

	private static final long serialVersionUID = 1L;

	private ScheduleJclDTO schedule;
	
	private Collection<LetGoalJcl> letGoals;
	
	private Collection<TotalScoreJcl> totalScores;
	
}




