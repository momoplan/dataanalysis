package com.ruyicai.dataanalysis.dto.lq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.domain.lq.TechnicCountJcl;

import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class ScheduleJclDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer scheduleId;
	
	private Integer sclassId;
	
	private String sclassName;
	
	private String sclassType;
	
	private String sclassShortName;
	
	private String homeTeamId;

	private String guestTeamId;

	private String homeTeam;

	private String guestTeam;
	
	private String homeTeamShortJ;

	private String guestTeamShortJ;
	
	private String homeTeamIco;
	
	private String guestTeamIco;
	
	private Integer homeTeamSupport = 0;
	
	private Integer guestTeamSupport = 0;
	
	private Date matchTime;
	
	private String homeScore;
	
	private String guestScore;
	
	private String homeOne;
	
	private String guestOne;
	
	private String homeTwo;
	
	private String guestTwo;
	
	private String homeThree;
	
	private String guestThree;
	
	private String homeFour;
	
	private String guestFour;
	
	private String addTime;
	
	private String homeAddTime1;
	
	private String guestAddTime1;
	
	private String homeAddTime2;
	
	private String guestAddTime2;
	
	private String homeAddTime3;
	
	private String guestAddTime3;
	
	private String matchState;
	
	private String remainTime;
	
	private String event;
	
	private String letScore;
	
	private String totalScore;
	
	private String turn;
	
	private Integer betState;
	
	private Date betEndTime;
	
	private Collection<TechnicCountJcl> technicCount = new ArrayList<TechnicCountJcl>();
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
	
}
