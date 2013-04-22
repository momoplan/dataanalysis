package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 大小(总分)盘赔率变化表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="TotalScoreDetail", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TotalScoreDetail {

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
