package com.ruyicai.dataanalysis.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class ProbabilityDto {

	private String event;
	
	private Double homeWinLu;
	
	private Double standoffLu;
	
	private Double guestWinLu;
	
}
