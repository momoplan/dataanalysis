package com.ruyicai.dataanalysis.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class RankingDTO implements Comparable<RankingDTO> {

	private int ranking = 0;
	
	private int teamID = 0;
	
	private String teamName = "";
	
	private int win = 0;
	
	private int standoff = 0;
	
	private int lose = 0;
	
	private int goinBall = 0;
	
	private int loseBall = 0;
	
	private int goalDifference = 0;
	
	private int integral = 0;
	
	private int matchcount = 0;

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
