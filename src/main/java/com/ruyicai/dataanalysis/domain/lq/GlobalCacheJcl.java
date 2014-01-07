package com.ruyicai.dataanalysis.domain.lq;

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

import com.ruyicai.dataanalysis.domain.cache.lq.GlobalCacheCacheJcl;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * 竞彩篮球-全局缓存
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooEntity(versionField="", table="globalcachejcl", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class GlobalCacheJcl {

	@Id
	@Column(name = "ID")
	private String id;
	
	private String value;
	
	@Autowired
	private transient GlobalCacheCacheJcl cache;
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        cache.setToMemcache(this);
    }
	
	@Transactional
    public GlobalCacheJcl merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        GlobalCacheJcl merged = this.entityManager.merge(this);
        this.entityManager.flush();
        cache.setToMemcache(merged);
        return merged;
    }
	
	public static GlobalCacheJcl findById(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager().find(GlobalCacheJcl.class, id);
    }
	
	public static GlobalCacheJcl findGlobalCache(String id) {
        return new GlobalCacheJcl().cache.getGlobalCacheJcl(id);
    }
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
	
	public static GlobalCacheJcl fromJsonToGlobalCache(String json) {
        return new JSONDeserializer<GlobalCacheJcl>().use(null, GlobalCacheJcl.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<GlobalCacheJcl> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<GlobalCacheJcl> fromJsonArrayToGlobalCaches(String json) {
        return new JSONDeserializer<List<GlobalCacheJcl>>().use(null, ArrayList.class).use("values", GlobalCacheJcl.class).deserialize(json);
    }
	
}
