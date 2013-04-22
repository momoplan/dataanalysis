package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 杯赛分组表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="CupMatch_Grouping", identifierField="groupID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class CupMatch_Grouping {
	
	@Id
	@Column(name = "GroupID")
	private int groupID;
	
	private Integer sclassID;

	private String matchSeason;

	private Integer isGroup;

	private String groupName;
	
	private String groupNameEn;
	
	private Integer GroupNum;

	private Integer isCurrentGroup;

	private Integer taxis;
	
	private Date addDateTime;

	private String lyMatch;

	private Integer lineCount;

	private String groupName_F;
}
