package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 亚洲杯积分榜主键数据
 * 
 * @author chenchuang   
 * @date 2014年12月17日上午11:48:42
 * @version V1.0   
 *
 */
@RooIdentifier
@RooToString
public class CupMatchJiFenPK {

	private static final long serialVersionUID = 1L;

	@Column(name = "GROUPING", length = 50)
	private String grouping;

	@Column(name = "TEAM", length = 100)
	private String team;
	
}




