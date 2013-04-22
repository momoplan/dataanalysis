package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.SclassCache;

import flexjson.JSON;

/**
 * @author fuqiang
 * 赛事类型表（联赛/杯赛）
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="Sclass", identifierField="sclassID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Sclass {
	
	@Id
	@Column(name = "sclassID")
	private int sclassID;
	
	private String name_J;
	
	private String name_F;
	
	private String name_E;
	
	private String name_JS;
	
	private String name_FS;
	
	private String name_ES;
	
	private String name_S;

	private Integer kind;

	private Integer count_round;

	private Integer curr_round;

	private String curr_matchSeason;

	private Integer infoID;

	private Integer subSclassID;
	
	private Integer isRanking;
	
	@Autowired
	private transient SclassCache sclassCache;
	
	@JSON(include = false)
	public SclassCache getSclassCache() {
		return sclassCache;
	}

	public void setSclassCache(SclassCache sclassCache) {
		this.sclassCache = sclassCache;
	}

	public static Sclass findByID(int sclassID) {
        return entityManager().find(Sclass.class, sclassID);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        sclassCache.setToMemcache(this);
    }
	
	@Transactional
    public Sclass merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Sclass merged = this.entityManager.merge(this);
        this.entityManager.flush();
        sclassCache.setToMemcache(merged);
        return merged;
    }
	
	public static Sclass findSclass(int teamID) {
		return new Sclass().getSclassCache().getSclass(teamID);
	}
	
}
