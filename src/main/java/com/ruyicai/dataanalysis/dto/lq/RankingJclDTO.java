package com.ruyicai.dataanalysis.dto.lq;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class RankingJclDTO implements Comparable<RankingJclDTO> {

	private int ranking;
	
	private int teamId; //球队编号
	
	private String teamName; //球队名称
	
	private int matchCount; //赛几场
	
	private int winCount; //胜次数
	
	private int loseCount; //负次数
	
	private double gainScore; //得分
	
	private double loseScore; //失分
	
	private double scoreDifference; //净得分
	
	private double winLv; //胜率
	
	@Override
	public int compareTo(RankingJclDTO o) {
		if(this.winLv < o.winLv) {
			return 1;
		}
		if(this.winLv > o.winLv) {
			return -1;
		}
		return 0;
	}
	
}
