package com.ruyicai.dataanalysis.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class KaiLiDto {

	private String event;
	
	private Double k_h;
	
	private Double k_s;
	
	private Double k_g;
	
}
