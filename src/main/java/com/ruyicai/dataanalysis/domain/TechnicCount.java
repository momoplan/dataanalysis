package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 技术统计表
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="techniccount", identifierField="scheduleId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TechnicCount {
	
	@Id
	@Column(name = "scheduleId")
	private int scheduleId;
	
	private String trapTime; //控球时间
	
	private String shootCount; //射门次数
	
	private String hitCount; //射中次数
	
	private String offsideCount; //越位次数
	
	private String cornerkickCount; //角球次数
	
	private String foulCount; //犯规次数
	
	private String yellowcardCount; //黄牌数
	
	private String redcardCount; //红牌数
	
}
