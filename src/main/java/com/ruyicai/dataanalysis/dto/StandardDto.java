package com.ruyicai.dataanalysis.dto;

import java.io.Serializable;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class StandardDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String scheduleId;
	
	private String event;
	
	private Double homeWin;
	
	private Double standoff;

	private Double guestWin;
	
}
