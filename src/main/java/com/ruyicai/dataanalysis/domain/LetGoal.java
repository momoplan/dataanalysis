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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.LetGoalCache;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author fuqiang
 * 让球盘赔率表
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="LetGoal", identifierField="oddsID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class LetGoal implements Comparable<LetGoal> {
	
	@Id
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "com.ruyicai.dataanalysis.util.AssignedSequenceGenerator", //
	parameters = {
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "LetGoal"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ"),
			@Parameter(name = TableGenerator.INCREMENT_PARAM, value = "1000")})
	@Column(name = "OddsID")
	private int oddsID;
	
	private Integer scheduleID;
	
	private Integer companyID;
	
	private Double firstGoal;
	
	private Double firstUpodds;
	
	private Double firstDownodds;

	private Double goal;
	
	private Double upOdds;

	private Double downOdds;

	private Date modifyTime;
	
	private Integer result;

	private Integer closePan;

	private Integer zouDi;

	private Integer running;
	
	private transient String companyName;
	
	private transient String companyName_e;
	
	private transient String firstGoal_name;
	
	private transient String goal_name;
	
	public String getFirstGoal_name() {
		return firstGoal_name;
	}

	public void setFirstGoal_name(String firstGoal_name) {
		this.firstGoal_name = firstGoal_name;
	}

	public String getGoal_name() {
		return goal_name;
	}

	public void setGoal_name(String goal_name) {
		this.goal_name = goal_name;
	}

	public void setFirstUpodds(Double firstUpodds) {
		this.firstUpodds = firstUpodds;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName_e() {
		return companyName_e;
	}

	public void setCompanyName_e(String companyName_e) {
		this.companyName_e = companyName_e;
	}
	
	@Autowired
	private transient LetGoalCache letGoalCache;
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        letGoalCache.setToMemcache(this);
    }
	
	@Transactional
    public LetGoal merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        LetGoal merged = this.entityManager.merge(this);
        this.entityManager.flush();
        letGoalCache.setToMemcache(merged);
        return merged;
    }

	public static LetGoal findLetGoal(Integer scheduleID, Integer companyID) {
		return new LetGoal().letGoalCache.getLetGoal(scheduleID, companyID);
		
		/*List<LetGoal> letGoals = entityManager().createQuery("select o from LetGoal o where scheduleID=? and companyID=?", LetGoal.class)
				.setParameter(1, scheduleID).setParameter(2, companyID).getResultList();
		if(null == letGoals || letGoals.isEmpty()) {
			return null;
		}
		return letGoals.get(0);*/
	}
	
	public static LetGoal findByScheduleIdCompanyId(Integer scheduleID, Integer companyID) {
		List<LetGoal> letGoals = entityManager().createQuery("select o from LetGoal o where scheduleID=? and companyID=?", LetGoal.class)
				.setParameter(1, scheduleID).setParameter(2, companyID).getResultList();
		if(null == letGoals || letGoals.isEmpty()) {
			return null;
		}
		return letGoals.get(0);
	}
	
	public static List<LetGoal> findByScheduleID(Integer scheduleID) {
		return entityManager().createQuery("select o from LetGoal o where scheduleID=? order by companyID asc", LetGoal.class)
				.setParameter(1, scheduleID).getResultList();
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
	
	public static LetGoal fromJsonToLetGoal(String json) {
        return new JSONDeserializer<LetGoal>().use(null, LetGoal.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<LetGoal> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<LetGoal> fromJsonArrayToLetGoals(String json) {
        return new JSONDeserializer<List<LetGoal>>().use(null, ArrayList.class).use("values", LetGoal.class).deserialize(json);
    }

	@Override
	public int compareTo(LetGoal o) {
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
				+ ((downOdds == null) ? 0 : downOdds.hashCode());
		result = prime * result
				+ ((firstDownodds == null) ? 0 : firstDownodds.hashCode());
		result = prime * result
				+ ((firstGoal == null) ? 0 : firstGoal.hashCode());
		result = prime * result
				+ ((firstUpodds == null) ? 0 : firstUpodds.hashCode());
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
		result = prime * result
				+ ((scheduleID == null) ? 0 : scheduleID.hashCode());
		result = prime * result + ((upOdds == null) ? 0 : upOdds.hashCode());
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
		LetGoal other = (LetGoal) obj;
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
		if (downOdds == null) {
			if (other.downOdds != null)
				return false;
		} else if (!downOdds.equals(other.downOdds))
			return false;
		if (firstDownodds == null) {
			if (other.firstDownodds != null)
				return false;
		} else if (!firstDownodds.equals(other.firstDownodds))
			return false;
		if (firstGoal == null) {
			if (other.firstGoal != null)
				return false;
		} else if (!firstGoal.equals(other.firstGoal))
			return false;
		if (firstUpodds == null) {
			if (other.firstUpodds != null)
				return false;
		} else if (!firstUpodds.equals(other.firstUpodds))
			return false;
		if (goal == null) {
			if (other.goal != null)
				return false;
		} else if (!goal.equals(other.goal))
			return false;
		if (scheduleID == null) {
			if (other.scheduleID != null)
				return false;
		} else if (!scheduleID.equals(other.scheduleID))
			return false;
		if (upOdds == null) {
			if (other.upOdds != null)
				return false;
		} else if (!upOdds.equals(other.upOdds))
			return false;
		return true;
	}
}
