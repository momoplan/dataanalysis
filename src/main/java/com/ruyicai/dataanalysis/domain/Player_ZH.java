package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 球员转会表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="Player_ZH", identifierField="zH_ID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Player_ZH {

	@Id
	@Column(name = "ZH_ID")
	private int zH_ID;
	
	private Integer playerID;
	
	private String xL_Date;

	private String team;

	private String money;

	private String place;

	private Integer score;

	private String teamNow;
	
	private Date transferTime;
	
	private Date modifyTime;

	private String zH_Season;
	
	private Integer type;

	private Integer ifHot;
	
	private Integer hotSortNumber;
}
