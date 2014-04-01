package com.ruyicai.dataanalysis.dto;

import java.io.Serializable;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class LetgoalDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double goal;
	
	private Double upOdds;

	private Double downOdds;
	
}
