package com.ruyicai.dataanalysis.service.dto.jcl;

import java.util.Date;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import flexjson.JSONSerializer;

@RooJavaBean
@RooJson
public class ScheduleJclDTO {
	
	private Integer scheduleId;
	
	private Integer sclassId;
	
	private String sclassName;
	
	private String sclassType;
	
	private String sclassShortName;
	
	private String homeTeamId;

	private String guestTeamId;

	private String homeTeam;

	private String guestTeam;
	
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
	
	private String matchState;
	
	private String event;
	
	private String letScore;
	
	private String totalScore;
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }
	
}
