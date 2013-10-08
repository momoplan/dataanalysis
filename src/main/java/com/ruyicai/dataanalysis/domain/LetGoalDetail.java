package com.ruyicai.dataanalysis.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import com.ruyicai.dataanalysis.util.Page;

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
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ"),
			@Parameter(name = TableGenerator.INCREMENT_PARAM, value = "100")})
	@Column(name = "ID")
	private int id;
	
	private Integer oddsID;
	
	private Double upOdds;

	private Double goal;

	private Double downOdds;

	private Date modifyTime;

	private Integer isEarly;
	
	private transient String goalName;

	public String getGoalName() {
		return goalName;
	}

	public void setGoalName(String goalName) {
		this.goalName = goalName;
	}

	public static void findByOddsId(Integer oddsId, Page<LetGoalDetail> page) {
		TypedQuery<LetGoalDetail> query = entityManager()
				.createQuery("select o from LetGoalDetail o where isEarly=0 and oddsID=? order by modifyTime asc", LetGoalDetail.class)
				.setParameter(1, oddsId);
		query.setFirstResult(page.getPageIndex() * page.getMaxResult())
		.setMaxResults(page.getMaxResult());
		page.setList(query.getResultList());
		
		TypedQuery<Long> totalQuery = entityManager()
		.createQuery("select count(o) from LetGoalDetail o where isEarly=0 and oddsID=?", Long.class)
		.setParameter(1, oddsId);
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}
	
}
