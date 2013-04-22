package com.ruyicai.dataanalysis.domain.jcl;

import java.util.ArrayList;
import java.util.Collection;
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
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * 竞彩篮球-亚赔-总分盘
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="totalscorejcl", identifierField="oddsId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TotalScoreJcl implements Comparable<TotalScoreJcl> {
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "TotalScoreJcl"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "oddsId")
	private Integer oddsId;
	
	private Integer scheduleId;
	
	private Integer companyId;
	
	private Double firstGoal;
	
	private Double firstUpodds;
	
	private Double firstDownodds;
	
	private Double goal;
	
	private Double upOdds;

	private Double downOdds;
	
	private transient String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public static TotalScoreJcl findTotalScoreJcl(Integer scheduleId, Integer companyId) {
		List<TotalScoreJcl> totalScores = entityManager().createQuery("select o from TotalScoreJcl o where scheduleId=? and companyId=?", TotalScoreJcl.class)
				.setParameter(1, scheduleId).setParameter(2, companyId).getResultList();
		if(null == totalScores || totalScores.isEmpty()) {
			return null;
		}
		return totalScores.get(0);
	}
	
	public static List<TotalScoreJcl> findByScheduleID(Integer scheduleID) {
		return entityManager().createQuery("select o from TotalScoreJcl o where scheduleId=? order by companyId asc", TotalScoreJcl.class)
				.setParameter(1, scheduleID).getResultList();
	}
	
	public static String toJsonArray(Collection<TotalScoreJcl> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
	
	public static Collection<TotalScoreJcl> fromJsonArrayToTotalScoreJcls(String json) {
        return new JSONDeserializer<List<TotalScoreJcl>>().use(null, ArrayList.class).use("values", TotalScoreJcl.class).deserialize(json);
    }
	
	@Override
	public int compareTo(TotalScoreJcl o) {
		return this.companyId - o.companyId;
	}

}
