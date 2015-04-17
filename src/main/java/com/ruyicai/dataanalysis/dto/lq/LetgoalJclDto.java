package com.ruyicai.dataanalysis.dto.lq;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

/**
 * @Description: 篮球让球数
 * 
 * @author chenchuang   
 * @date 2015年3月13日下午3:20:35
 * @version V1.0   
 *
 */
@RooJavaBean
@RooJson
public class LetgoalJclDto {

	private static final long serialVersionUID = 1L;

	private String event = "";
	
	private Double goal = 0.0;
	
	private Double upOdds = 0.0;

	private Double downOdds = 0.0;
	
	private String goalName = "";
	
}




