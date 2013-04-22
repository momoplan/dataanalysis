package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="LetGoalhalfDetail", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class LetGoalhalfDetail {
	
	@Id
	@Column(name = "ID")
	private int id;
	
	private Integer oddsID;
	
	private Double upOdds;

	private Double goal;

	private Double downOdds;

	private Date modifyTime;
	
	private Integer isEarly;
}
