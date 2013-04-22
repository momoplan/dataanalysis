package com.ruyicai.dataanalysis.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 球员资料表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="Player", identifierField="playerID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Player {
	
	@Id
	@Column(name = "PlayerID")
	private int playerID;
	
	private Integer kind;

	private String name_short;

	private String name_F;

	private String name_J;
	
	private String name_E;
	
	private String name_Es;
	
	private String name_T;
	
	private String name_Y;

	private Date birthday;
	
	private Integer tallness;

	private Integer weight;
	
	private String country;

	private String photo;

	private String introduce;

	private String health;
	
	private Date modifyTime;

	private String countryEn;

	private String name_7M;

	private Integer isChecked;
}
