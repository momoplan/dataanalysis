package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 杯赛分组积分表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="CupMatch", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class CupMatch {
	
	@Id
	@Column(name = "ID")
	private int id;
	
	private Integer sclassID;
	
	private Integer cupMatch_Type;

	private String grouping;

	private Integer area;
	
	private String content;

	private String strContent;
	
	private String matchseason;

	private Integer lineCount;
	
	private Integer isUpdate;
}
