package com.ruyicai.dataanalysis.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.GlobalCacheCache;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author fuqiang
 * 全局缓存
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="GlobalCache", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class GlobalCache {

	@Id
	@Column(name = "ID")
	private String id;
	
	private String value;
	
	@Autowired
	private transient GlobalCacheCache cache;
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        cache.setToMemcache(this);
    }
	
	@Transactional
    public GlobalCache merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        GlobalCache merged = this.entityManager.merge(this);
        this.entityManager.flush();
        cache.setToMemcache(merged);
        return merged;
    }
	
	public static GlobalCache findById(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager().find(GlobalCache.class, id);
    }
	
	public static GlobalCache findGlobalCache(String id) {
        return new GlobalCache().cache.getGlobalCache(id);
    }
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
	
	public static GlobalCache fromJsonToGlobalCache(String json) {
        return new JSONDeserializer<GlobalCache>().use(null, GlobalCache.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<GlobalCache> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<GlobalCache> fromJsonArrayToGlobalCaches(String json) {
        return new JSONDeserializer<List<GlobalCache>>().use(null, ArrayList.class).use("values", GlobalCache.class).deserialize(json);
    }
}
