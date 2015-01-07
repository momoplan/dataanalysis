package com.ruyicai.dataanalysis.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 亚洲杯积分榜数据
 * 
 * @author chenchuang   
 * @date 2014年12月17日上午11:42:22
 * @version V1.0   
 *
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="CupMatchJiFen", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class CupMatchJiFen {
	
	@Id
	@EmbeddedId
	private CupMatchJiFenPK id;
	
	@Column(name = "RANKING")
	private int ranking;
	
	@Column(name = "TOTAL")
	private String total;
	
	@Column(name = "WIN")
	private String win;
	
	@Column(name = "PING")
	private String ping;
	
	@Column(name = "LOSS")
	private String loss;
	
	@Column(name = "GET")
	private String get;
	
	@Column(name = "MISS")
	private String miss;
	
	@Column(name = "JING")
	private String jing;
	
	@Column(name = "SCORE")
	private String score;
	
	public static List<CupMatchJiFen> findCupMatchJiFenByGrouping(String grouping) {
		List<CupMatchJiFen> cupMatch = entityManager().createQuery("select o from CupMatchJiFen o where o.id.grouping = ? order by o.ranking asc", CupMatchJiFen.class)
				.setParameter(1, grouping).getResultList();
		return cupMatch;
	}
}




