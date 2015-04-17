package com.ruyicai.dataanalysis.domain.lq;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 篮球技术统计表
 * 
 * @author chenchuang   
 * @date 2015年3月17日上午10:42:44
 * @version V1.0   
 *
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="techniccountjcl", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TechnicCountJcl {

	@Id
	@EmbeddedId
	private TechnicCountJclPK id;
	
	@Column(name = "player")
	private String player;	//球员名
	
	@Column(name = "location")
	private String location;	//位置
	
	@Column(name = "playtime")
	private String playtime;	//上场时间
	
	@Column(name = "shoot")
	private String shoot;	//投篮数
	
	@Column(name = "threemin")
	private String threemin;	//三分
	
	@Column(name = "punishball")
	private String punishball;	//罚球
	
	@Column(name = "attack")
	private String attack;	//进攻
	
	@Column(name = "defend")
	private String defend;	//防守
	
	@Column(name = "helpattack")
	private String helpattack;	//助攻
	
	@Column(name = "foul")
	private String foul;	//犯规
	
	@Column(name = "rob")
	private String rob;	//抢断
	
	@Column(name = "misplay")
	private String misplay;	//失误
	
	@Column(name = "cover")
	private String cover;	//盖帽
	
	@Column(name = "score")
	private String score;	//得分
	
	
	public static List<TechnicCountJcl> findTechnicCountJcl(String event) {
		TypedQuery<TechnicCountJcl> query = entityManager()
				.createQuery(
						"select o from TechnicCountJcl o where o.id.scheduleId in (select scheduleId from ScheduleJcl where event = ? )",
						TechnicCountJcl.class);
		query.setParameter(1, event);
		List<TechnicCountJcl> technicCountJcls = query.getResultList();
		return technicCountJcls;
	}
	
}




