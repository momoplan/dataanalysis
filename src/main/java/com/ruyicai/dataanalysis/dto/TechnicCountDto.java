package com.ruyicai.dataanalysis.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import com.ruyicai.dataanalysis.domain.TechnicCount;

@RooJavaBean
@RooJson
public class TechnicCountDto {

	private ScheduleDTO schedule;
	
	private TechnicCount technicCount;
	
}
