package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 半场积分表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="HalfScore", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class HalfScore {
	
	@Id
	@Column(name = "ID")
	private int id;
	
	private Integer teamID;

	private Integer sclassID;
	
	private Integer win_Score;
	
	private Integer flat_Score;
	
	private Integer fail_Score;
	
	private Integer total_Homescore;
	
	private Integer total_Guestscore;
	
	private Integer homeorguest;

	private String matchseason;
	
	private Integer subSclassID;
}
