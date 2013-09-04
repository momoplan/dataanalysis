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
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @author fuqiang
 * 让球盘赔率变化表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="LetGoalDetail", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class LetGoalDetail {
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "LetGoalDetail"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "ID")
	private int id;
	
	private Integer oddsID;
	
	private Double upOdds;

	private Double goal;

	private Double downOdds;

	private Date modifyTime;

	private Integer isEarly;
	
	public static List<LetGoalDetail> findByOddsId(Integer oddsId) {
		List<LetGoalDetail> letGoalDetail = entityManager().createQuery("select o from LetGoalDetail o where isEarly=0 and oddsID=? order by modifyTime asc", LetGoalDetail.class)
				.setParameter(1, oddsId).getResultList();
		return letGoalDetail;
	}
	
}
