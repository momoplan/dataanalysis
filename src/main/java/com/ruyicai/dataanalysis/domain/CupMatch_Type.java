package com.ruyicai.dataanalysis.domain;

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
@RooEntity(versionField="", table="CupMatch_Type", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class CupMatch_Type {

	@Id
	@Column(name = "ID")
	private int id;
	
	private Integer sclassID;
	
	private Integer yuxuan;

	private Integer one;
	
	private Integer two;
	
	private Integer three;

	private Integer group_one;
	
	private Integer group_two;
	
	private Integer out_w;

	private Integer add_match;

	private Integer four;
	
	private Integer five;
	
	private Integer thiry_two;
	
	private Integer six;

	private Integer elimination;

	private Integer eight;

	private Integer sixteen;
	
	private Integer halfz_match;
	
	private Integer z_match;

	private Integer jj_match;
	
	private Integer j_match;

	private Integer curr_type;
	
	private Integer area;

	private Integer mc_match;
}
