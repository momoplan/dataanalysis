package com.ruyicai.dataanalysis.dto.zc;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

/**
 * 足彩对阵
 * @author Administrator
 *
 */
@RooJavaBean
@RooJson
public class DuiZhenZcDTO {

	private String teamId; //场次编号
	
	private String leagueName; //联赛名称
	
	private String homeTeam; //主队名称
	
	private String guestTeam; //客队名称
	
	private String matchTime; //比赛时间
	
	private String homeWinAverageOuPei; //主胜平均欧赔
	
	private String standoffAverageOuPei; //平局平均欧赔
	
	private String guestWinAverageOuPei; //客胜平均欧赔
	
}
