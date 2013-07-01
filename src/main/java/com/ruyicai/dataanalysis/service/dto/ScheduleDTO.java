package com.ruyicai.dataanalysis.service.dto;

import java.util.Collection;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.domain.DetailResult;

import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class ScheduleDTO {
	
	private int scheduleID;
	
	private Integer sclassID;

	private String matchSeason;

	private Integer round;

	private String grouping;

	private Integer homeTeamID;

	private Integer guestTeamID;

	private String homeTeam;

	private String guestTeam;

	private Integer neutrality;
	
	private Date matchTime;
	
	private Date matchTime2;
	
	private String location;
	
	private String home_Order;
	
	private String guest_Order;
	
	private Integer matchState;
	
	private Integer weatherIcon;
	
	private String weather;
	
	private String temperature;
	
	private Integer homeScore;
	
	private Integer guestScore;

	private Integer homeHalfScore;
	
	private Integer guestHalfScore;

	private Integer home_Red;
	
	private Integer guest_Red;

	private Integer home_Yellow;

	private Integer guest_Yellow;
	
	private String sclassName;
	
	private String sclassName_j;
	
	private String event;
	
	private String turn;
	
	private String zcSfcTurn;
	
	private String zcJqcTurn;
	
	private String zcBqcTurn;
	
	private String bdTurn;
	
	private Collection<DetailResult> detailResults;
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
}
