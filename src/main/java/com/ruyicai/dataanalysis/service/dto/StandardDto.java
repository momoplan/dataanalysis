package com.ruyicai.dataanalysis.service.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class StandardDto {

	//private String companyId;
	
	private Double homeWin;
	
	private Double standoff;

	private Double guestWin;
	
}
