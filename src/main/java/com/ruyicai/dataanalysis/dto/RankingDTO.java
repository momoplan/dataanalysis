package com.ruyicai.dataanalysis.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class RankingDTO implements Comparable<RankingDTO> {

	private int ranking;
	
	private int teamID;
	
	private String teamName;
	
	private int win;
	
	private int standoff;
	
	private int lose;
	
	private int goinBall;
	
	private int loseBall;
	
	private int goalDifference;
	
	private int integral;
	
	private int matchcount;

	@Override
	public int compareTo(RankingDTO o) {
		if(this.integral < o.integral) {
			return 1;
		}
		if(this.integral > o.integral) {
			return -1;
		}
		if(this.goalDifference < o.goalDifference) {
			return 1;
		}
		if(this.getGoalDifference() > o.goalDifference) {
			return -1;
		}
		return 0 - (this.goinBall - o.goinBall);
	}
}
