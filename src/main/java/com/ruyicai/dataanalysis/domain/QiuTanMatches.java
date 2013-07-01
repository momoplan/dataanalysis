package com.ruyicai.dataanalysis.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 彩票赛事与球探网的关联表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="QiuTanMatches", identifierField="matchId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class QiuTanMatches {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MATCHID")
	private int matchId;
	
	private String lotteryName;
	
	private String issueNum;
	
	private String id;
	
	private Integer iD_bet007;
	
	private Date time;
	
	private String home;
	
	private String away;
	
	private Integer homeID;
	
	private Integer awayID;
	
	private String event;
	
	private String zcSfcEvent;
	
	private String zcJqcEvent;
	
	private String zcBqcEvent;
	
	private String bdEvent;
	
	private String turn;
	
	private String zcSfcTurn;
	
	private String zcJqcTurn;
	
	private String zcBqcTurn;
	
	private String bdTurn;
	
	public static QiuTanMatches findByID_bet007(int id_bet007, String lotteryName) {
		List<QiuTanMatches> qiuTanMatches = entityManager().createQuery("select o from QiuTanMatches o where iD_bet007=? and lotteryName=?", QiuTanMatches.class)
			.setParameter(1, id_bet007).setParameter(2, lotteryName).getResultList();
		if(null == qiuTanMatches || qiuTanMatches.isEmpty()) {
			return null;
		}
		return qiuTanMatches.get(0);
	}
	
	public static QiuTanMatches findByLotteryName_issueNum_id(String lotteryName, String issueNum, String id) {
		List<QiuTanMatches> qiuTanMatches = entityManager().createQuery("select o from QiuTanMatches o where lotteryName=? and issueNum=? and id=?", QiuTanMatches.class)
		.setParameter(1, lotteryName).setParameter(2, issueNum).setParameter(3, id).getResultList();
		if(null == qiuTanMatches || qiuTanMatches.isEmpty()) {
			return null;
		}
		return qiuTanMatches.get(0);
	}
	
}
