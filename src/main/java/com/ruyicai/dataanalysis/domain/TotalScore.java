package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 大小(总分)盘赔率表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="TotalScore", identifierField="oddsID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TotalScore {

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

	private Integer result;

	private Integer closePan;

	private Integer zoudi;

	private Double goal_real;

	private Double upOdds_real;

	private Double downOdds_real;
}
