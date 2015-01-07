package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 亚洲杯射手榜主键数据
 * 
 * @author chenchuang   
 * @date 2014年12月25日下午1:36:10
 * @version V1.0   
 *
 */
@RooIdentifier
@RooToString
public class CupMatchRankingPK {

	private static final long serialVersionUID = 1L;

	@Column(name = "RANKING")
	private int ranking;
	
	@Column(name = "LEAGUE")
	private String league;
	
	@Column(name = "SEASON")
	private String season;
	
}




