package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 球员所属球队表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="PlayerInTeam", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class PlayerInTeam {

	@Id
	@Column(name = "ID")
	private int id;
	
	private Integer playerID;

	private String playerName;
	
	private Integer teamID;
	
	private String teamName;

	private String place;

	private String number;

	private Integer score;

	private Date modifyTime;
	
	private String msrepl_tran_version;
}
