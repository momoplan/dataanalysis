package com.ruyicai.dataanalysis.dto;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import com.ruyicai.dataanalysis.domain.LetGoal;

@RooJavaBean
@RooJson
public class LetgoalsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private ScheduleDTO schedule;
	
	private Collection<LetGoal> letGoals;
	
}
