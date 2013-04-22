package com.ruyicai.dataanalysis.domain.jcl;

import java.util.ArrayList;
import java.util.Collection;
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
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * 竞彩篮球-欧赔
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="standardjcl", identifierField="oddsId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class StandardJcl implements Comparable<StandardJcl> {

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "StandardJcl"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "oddsId")
	private Integer oddsId;
	
	private Integer scheduleId;
	
	private Integer companyId;
	
	private Double firstHomeWin;
	
	private Double firstGuestWin;

	private Double homeWin;
	
	private Double guestWin;
	
	private Date modifyTime;
	
	private transient String companyName;
	
	private transient Double homeWinLv;
	
	private transient Double guestWinLv;
	
	private transient Double fanHuanLv;
	
	private transient Double k_h;
	
	private transient Double k_g;
	
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getHomeWinLv() {
		return homeWinLv;
	}

	public void setHomeWinLv(Double homeWinLv) {
		this.homeWinLv = homeWinLv;
	}

	public Double getGuestWinLv() {
		return guestWinLv;
	}

	public void setGuestWinLv(Double guestWinLv) {
		this.guestWinLv = guestWinLv;
	}

	public Double getFanHuanLv() {
		return fanHuanLv;
	}

	public void setFanHuanLv(Double fanHuanLv) {
		this.fanHuanLv = fanHuanLv;
	}
	
	public Double getK_h() {
		return k_h;
	}

	public void setK_h(Double k_h) {
		this.k_h = k_h;
	}

	public Double getK_g() {
		return k_g;
	}

	public void setK_g(Double k_g) {
		this.k_g = k_g;
	}

	public static StandardJcl findStandardJcl(Integer scheduleId, Integer companyId) {
		List<StandardJcl> standardJcls = entityManager().createQuery("select o from StandardJcl o where scheduleId=? and companyId=?", StandardJcl.class)
				.setParameter(1, scheduleId).setParameter(2, companyId).getResultList();
		if(null == standardJcls || standardJcls.isEmpty()) {
			return null;
		}
		return standardJcls.get(0);
	}
	
	public static List<StandardJcl> findByScheduleID(Integer scheduleID) {
		return entityManager().createQuery("select o from StandardJcl o, EuropeCompanyJcl c where o.companyId=c.companyId and c.nameC is not null and o.scheduleId=? order by o.companyId asc", StandardJcl.class)
				.setParameter(1, scheduleID).getResultList();
	}
	
	public static String toJsonArray(Collection<StandardJcl> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
	
	public static Collection<StandardJcl> fromJsonArrayToStandardJcls(String json) {
        return new JSONDeserializer<List<StandardJcl>>().use(null, ArrayList.class).use("values", StandardJcl.class).deserialize(json);
    }

	@Override
	public int compareTo(StandardJcl o) {
		return this.companyId - o.companyId;
	}
	
}
