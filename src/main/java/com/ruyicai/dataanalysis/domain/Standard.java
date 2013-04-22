package com.ruyicai.dataanalysis.domain;

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
 * @author fuqiang
 * 标准（欧）盘赔率表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="Standard", identifierField="oddsID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Standard implements Comparable<Standard> {

	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "Standard"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "OddsID")
	private int oddsID;
	
	private Integer scheduleID;
	
	private Integer companyID;

	private Double firstHomeWin;
	
	private Double firstStandoff;

	private Double firstGuestWin;

	private Double homeWin;
	
	private Double standoff;

	private Double guestWin;

	private Date modifyTime;

	private Integer result;

	private Integer closePan;
	
	private transient Double homeWinLu;
	
	private transient Double standoffLu;
	
	private transient Double guestWinLu;
	
	private transient Double fanHuanLu;
	
	private transient String companyName;
	
	private transient String companyName_e;
	
	private transient Integer isPrimary;
	
	private transient Integer isExchange;
	
	private transient Double k_h;
	
	private transient Double k_s;
	
	private transient Double k_g;
	
	public Double getK_h() {
		return k_h;
	}

	public void setK_h(Double k_h) {
		this.k_h = k_h;
	}

	public Double getK_s() {
		return k_s;
	}

	public void setK_s(Double k_s) {
		this.k_s = k_s;
	}

	public Double getK_g() {
		return k_g;
	}

	public void setK_g(Double k_g) {
		this.k_g = k_g;
	}

	public String getCompanyName_e() {
		return companyName_e;
	}

	public void setCompanyName_e(String companyName_e) {
		this.companyName_e = companyName_e;
	}

	public Integer getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Integer isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Integer getIsExchange() {
		return isExchange;
	}

	public void setIsExchange(Integer isExchange) {
		this.isExchange = isExchange;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Double getHomeWinLu() {
		return homeWinLu;
	}

	public void setHomeWinLu(Double homeWinLu) {
		this.homeWinLu = homeWinLu;
	}

	public Double getStandoffLu() {
		return standoffLu;
	}

	public void setStandoffLu(Double standoffLu) {
		this.standoffLu = standoffLu;
	}

	public Double getGuestWinLu() {
		return guestWinLu;
	}

	public void setGuestWinLu(Double guestWinLu) {
		this.guestWinLu = guestWinLu;
	}

	public Double getFanHuanLu() {
		return fanHuanLu;
	}

	public void setFanHuanLu(Double fanHuanLu) {
		this.fanHuanLu = fanHuanLu;
	}

	public static Standard findStandard(Integer scheduleID, Integer companyID) {
		List<Standard> standards = entityManager().createQuery("select o from Standard o where scheduleID=? and companyID=?", Standard.class)
				.setParameter(1, scheduleID).setParameter(2, companyID).getResultList();
		if(null == standards || standards.isEmpty()) {
			return null;
		}
		return standards.get(0);
	}
	
	public static List<Standard> findByScheduleID(Integer scheduleID) {
		return entityManager().createQuery("select o from Standard o, EuropeCompany c where o.companyID=c.companyID and c.name_Cn is not null and o.scheduleID=? and c.isPrimary=1 order by o.companyID asc", Standard.class)
				.setParameter(1, scheduleID).getResultList();
	}
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
	
	public static Standard fromJsonToStandard(String json) {
        return new JSONDeserializer<Standard>().use(null, Standard.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<Standard> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Standard> fromJsonArrayToStandards(String json) {
        return new JSONDeserializer<List<Standard>>().use(null, ArrayList.class).use("values", Standard.class).deserialize(json);
    }

	@Override
	public int compareTo(Standard o) {
		return this.companyID - o.companyID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((companyID == null) ? 0 : companyID.hashCode());
		result = prime * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result
				+ ((companyName_e == null) ? 0 : companyName_e.hashCode());
		result = prime * result
				+ ((fanHuanLu == null) ? 0 : fanHuanLu.hashCode());
		result = prime * result
				+ ((firstGuestWin == null) ? 0 : firstGuestWin.hashCode());
		result = prime * result
				+ ((firstHomeWin == null) ? 0 : firstHomeWin.hashCode());
		result = prime * result
				+ ((firstStandoff == null) ? 0 : firstStandoff.hashCode());
		result = prime * result
				+ ((guestWin == null) ? 0 : guestWin.hashCode());
		result = prime * result
				+ ((guestWinLu == null) ? 0 : guestWinLu.hashCode());
		result = prime * result + ((homeWin == null) ? 0 : homeWin.hashCode());
		result = prime * result
				+ ((homeWinLu == null) ? 0 : homeWinLu.hashCode());
		result = prime * result
				+ ((isExchange == null) ? 0 : isExchange.hashCode());
		result = prime * result
				+ ((isPrimary == null) ? 0 : isPrimary.hashCode());
		result = prime * result + ((k_g == null) ? 0 : k_g.hashCode());
		result = prime * result + ((k_h == null) ? 0 : k_h.hashCode());
		result = prime * result + ((k_s == null) ? 0 : k_s.hashCode());
		result = prime * result
				+ ((scheduleID == null) ? 0 : scheduleID.hashCode());
		result = prime * result
				+ ((standoff == null) ? 0 : standoff.hashCode());
		result = prime * result
				+ ((standoffLu == null) ? 0 : standoffLu.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Standard other = (Standard) obj;
		if (companyID == null) {
			if (other.companyID != null)
				return false;
		} else if (!companyID.equals(other.companyID))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (companyName_e == null) {
			if (other.companyName_e != null)
				return false;
		} else if (!companyName_e.equals(other.companyName_e))
			return false;
		if (fanHuanLu == null) {
			if (other.fanHuanLu != null)
				return false;
		} else if (!fanHuanLu.equals(other.fanHuanLu))
			return false;
		if (firstGuestWin == null) {
			if (other.firstGuestWin != null)
				return false;
		} else if (!firstGuestWin.equals(other.firstGuestWin))
			return false;
		if (firstHomeWin == null) {
			if (other.firstHomeWin != null)
				return false;
		} else if (!firstHomeWin.equals(other.firstHomeWin))
			return false;
		if (firstStandoff == null) {
			if (other.firstStandoff != null)
				return false;
		} else if (!firstStandoff.equals(other.firstStandoff))
			return false;
		if (guestWin == null) {
			if (other.guestWin != null)
				return false;
		} else if (!guestWin.equals(other.guestWin))
			return false;
		if (guestWinLu == null) {
			if (other.guestWinLu != null)
				return false;
		} else if (!guestWinLu.equals(other.guestWinLu))
			return false;
		if (homeWin == null) {
			if (other.homeWin != null)
				return false;
		} else if (!homeWin.equals(other.homeWin))
			return false;
		if (homeWinLu == null) {
			if (other.homeWinLu != null)
				return false;
		} else if (!homeWinLu.equals(other.homeWinLu))
			return false;
		if (isExchange == null) {
			if (other.isExchange != null)
				return false;
		} else if (!isExchange.equals(other.isExchange))
			return false;
		if (isPrimary == null) {
			if (other.isPrimary != null)
				return false;
		} else if (!isPrimary.equals(other.isPrimary))
			return false;
		if (k_g == null) {
			if (other.k_g != null)
				return false;
		} else if (!k_g.equals(other.k_g))
			return false;
		if (k_h == null) {
			if (other.k_h != null)
				return false;
		} else if (!k_h.equals(other.k_h))
			return false;
		if (k_s == null) {
			if (other.k_s != null)
				return false;
		} else if (!k_s.equals(other.k_s))
			return false;
		if (scheduleID == null) {
			if (other.scheduleID != null)
				return false;
		} else if (!scheduleID.equals(other.scheduleID))
			return false;
		if (standoff == null) {
			if (other.standoff != null)
				return false;
		} else if (!standoff.equals(other.standoff))
			return false;
		if (standoffLu == null) {
			if (other.standoffLu != null)
				return false;
		} else if (!standoffLu.equals(other.standoffLu))
			return false;
		return true;
	}
}
