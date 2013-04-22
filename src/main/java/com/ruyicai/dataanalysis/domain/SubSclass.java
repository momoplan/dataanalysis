package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 子联赛表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="SubSclass", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class SubSclass {

	@Id
	@Column(name = "subSclassID")
	private int subSclassID;

	private Integer sclassid;

	private Integer isHaveScore;

	private Integer sortNumber;

	private Integer curr_round;

	private Integer count_round;

	private Integer isCurrentSclass;

	private String subSclassName;

	private String subSclassNameEn;

	private String subName_Js;

	private String subName_Es;
	
	private String subName_Fs;

	private String includeSeason;

	private Integer isAnalyScore;

	private String subSclass_F;
}
