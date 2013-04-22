package com.ruyicai.dataanalysis.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 比赛详细结果表
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="DetailResult", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class DetailResult {
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "DetailResult"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "ID")
	private int id;
	
	private Integer scheduleID;
	
	private Integer happenTime;

	private Integer teamID;
	
	private String playername;

	private Integer playerID;
	
	private Integer kind;

	private Date modifyTime;
	
	private String playername_e;

	private String playername_j;
	
	private Integer deleteState;
	
	public static DetailResult findDetailResult(int scheduleID, Integer happenTime, Integer kind, Integer teamID, Integer playerID) {
		List<DetailResult> results = entityManager().createQuery("select o from DetailResult o where deleteState=1 and scheduleID=? and happenTime=? and kind=? and teamID=? and playerID=?", DetailResult.class)
				.setParameter(1, scheduleID).setParameter(2, happenTime).setParameter(3, kind).setParameter(4, teamID).setParameter(5, playerID).getResultList();
		if(null == results || results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
	
	public static List<DetailResult> findDetailResults(int scheduleID) {
		return entityManager().createQuery("select o from DetailResult o where deleteState=1 and scheduleID=? order by happenTime asc", DetailResult.class)
				.setParameter(1, scheduleID).getResultList();
	}
	
}
