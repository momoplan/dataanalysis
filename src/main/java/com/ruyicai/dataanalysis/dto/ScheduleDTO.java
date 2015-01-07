package com.ruyicai.dataanalysis.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.domain.DetailResult;

import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class ScheduleDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int scheduleID = 0;
	
	private Integer sclassID = 0;

	private String matchSeason = "";

	private Integer round = 0;

	private String grouping = "";

	private Integer homeTeamID = 0;

	private Integer guestTeamID = 0;

	private String homeTeam = "";

	private String guestTeam = "";
	
	private String homeTeamIco = "";
	
	private String guestTeamIco = "";
	
	private Integer homeTeamSupport = 0;
	
	private Integer guestTeamSupport = 0;
	
	private Integer neutrality = 0;
	
	private Date matchTime = null;
	
	private Date matchTime2 = null;
	
	private String location = "";
	
	private String home_Order = "";
	
	private String guest_Order = "";
	
	private Integer matchState = 0;
	
	private Integer weatherIcon = 0;
	
	private String weather = "";
	
	private String temperature = "";
	
	private Integer homeScore = 0;
	
	private Integer guestScore = 0;

	private Integer homeHalfScore = 0;
	
	private Integer guestHalfScore = 0;

	private Integer home_Red = 0;
	
	private Integer guest_Red = 0;

	private Integer home_Yellow = 0;

	private Integer guest_Yellow = 0;
	
	private String sclassName = "";
	
	private String sclassName_j = "";
	
	private String event = "";
	
	private String bdEvent = "";
	
	private String turn = "";
	
	private String zcSfcTurn = "";
	
	private String zcJqcTurn = "";
	
	private String zcBqcTurn = "";
	
	private String bdTurn = "";
	
	private Integer betState = 0;
	
	private Date betEndTime = null;
	
	private String isAddTime;	//是否有加时（0：无；1：有）
	
	private String matchExplain; //比赛说明，加时点球结果
	
	private Collection<DetailResult> detailResults;
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
}
