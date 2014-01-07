package com.ruyicai.dataanalysis.domain.lq;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.domain.cache.lq.SclassJclCache;

import flexjson.JSON;

/**
 * 竞彩篮球赛事类型
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="sclassjcl", identifierField="sclassId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class SclassJcl {
	
	@Id
	@Column(name = "sclassId")
	private Integer sclassId;
	
	private String nameJs;
	
	private String nameJ;
	
	private String type;
	
	private String currentMatchSeason;
	
	private String sclassKind;
	
	private String sclassTime;
	
	@Autowired
	private transient SclassJclCache sclassJclCache;
	
	@JSON(include = false)
	public SclassJclCache getSclassJclCache() {
		return sclassJclCache;
	}

	public void setSclassJclCache(SclassJclCache sclassJclCache) {
		this.sclassJclCache = sclassJclCache;
	}

	public static SclassJcl findByID(int sclassId) {
        return entityManager().find(SclassJcl.class, sclassId);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        sclassJclCache.setToMemcache(this);
    }
	
	@Transactional
    public SclassJcl merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SclassJcl merged = this.entityManager.merge(this);
        this.entityManager.flush();
        sclassJclCache.setToMemcache(merged);
        return merged;
    }
	
	public static SclassJcl findSclassJcl(int teamId) {
		return new SclassJcl().getSclassJclCache().getSclassJcl(teamId);
	}
	
}
