package com.ruyicai.dataanalysis.dto;

import java.io.Serializable;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class LetgoalDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String event = "";
	
	private Double goal = 0.0;
	
	private Double upOdds = 0.0;

	private Double downOdds = 0.0;
	
	private String goalName = "";
	
}
