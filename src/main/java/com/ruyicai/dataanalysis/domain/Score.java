package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 积分表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="Score", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Score {

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

	private Integer deduct;

	private String cause;

	private Integer goal;

	private String causeEn;

	private Integer subSclassID;

	private Integer redCard;
}
