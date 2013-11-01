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
 * 标准（欧）盘赔率变化表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="StandardDetail", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class StandardDetail {

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "StandardDetail"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ"),
			@Parameter(name = TableGenerator.INCREMENT_PARAM, value = "1000"),
			@Parameter(name = TableGenerator.OPT_PARAM, value = "pooled-lo")})
	@Column(name = "ID")
	private int id;
	
	private Integer oddsID;

	private Double homeWin;
	
	private Double standoff;
	
	private Double guestWin;

	private Date modifyTime;
	
	private Integer isEarly;
	
	public static void findByOddsId(Integer oddsId, Page<StandardDetail> page) {
		TypedQuery<StandardDetail> query = entityManager()
				.createQuery("select o from StandardDetail o where isEarly=0 and oddsID=? order by modifyTime desc", StandardDetail.class)
				.setParameter(1, oddsId);
		query.setFirstResult(page.getPageIndex() * page.getMaxResult())
		.setMaxResults(page.getMaxResult());
		page.setList(query.getResultList());
		
		TypedQuery<Long> totalQuery = entityManager()
				.createQuery("select count(o) from StandardDetail o where isEarly=0 and oddsID=?", Long.class)
				.setParameter(1, oddsId);
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}
	
}
