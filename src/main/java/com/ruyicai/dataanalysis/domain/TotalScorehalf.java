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
@RooEntity(versionField="", table="TotalScorehalf", identifierField="oddsID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TotalScorehalf {

	@Id
	@Column(name = "OddsID")
	private int oddsID;
	
	private Integer scheduleID;

	private Integer companyID;

	private Double firstGoal;

	private Double firstUpodds;

	private Double firstDownodds;

	private Double goal;

	private Double upOdds;

	private Double downOdds;

	private Date modifyTime;

	private Integer zoudi;
}
