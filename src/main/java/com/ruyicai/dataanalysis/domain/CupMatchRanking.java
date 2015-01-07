package com.ruyicai.dataanalysis.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 亚洲杯射手榜数据
 * 
 * @author chenchuang
 * @date 2014年12月25日下午1:14:37
 * @version V1.0
 * 
 */
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "CupMatchRanking", identifierField = "id", persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class CupMatchRanking {

	@Id
	@EmbeddedId
	private CupMatchRankingPK id;

	@Column(name = "PLAYER")
	private String player;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "TEAM")
	private String team;

	@Column(name = "GOALS")
	private String goals;

	public static List<CupMatchRanking> findCupMatchRanking(String league, String season) {
		List<CupMatchRanking> cupMatch = entityManager()
				.createQuery(
						"select o from CupMatchRanking o where o.id.league = ? and o.id.season = ? order by o.id.ranking asc",
						CupMatchRanking.class).setParameter(1, league).setParameter(2, season).getResultList();
		return cupMatch;
	}
}
