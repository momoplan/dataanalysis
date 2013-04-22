package com.ruyicai.dataanalysis.domain.jcl;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.jcl.CompanyJclCache;
import flexjson.JSON;

/**
 * 亚赔公司
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="companyjcl", identifierField="companyId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class CompanyJcl {

	@Id
	@Column(name = "companyId")
	private Integer companyId;
	
	@Column(name = "kind")
	private String kind;
	
	@Column(name = "companyName")
	private String companyName;
	
	@Autowired
	private transient CompanyJclCache companyJclCache;
	
	@JSON(include = false)
	public CompanyJclCache getCompanyJclCache() {
		return companyJclCache;
	}

	public void setCompanyJclCache(CompanyJclCache companyJclCache) {
		this.companyJclCache = companyJclCache;
	}

	public static CompanyJcl findByID(int companyId) {
        return entityManager().find(CompanyJcl.class, companyId);
    }
	
	public static CompanyJcl findCompanyJcl(int companyId) {
        return new CompanyJcl().getCompanyJclCache().getCompanyJcl(companyId);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        try {
        	this.entityManager.persist(this);
        	companyJclCache.setToMemcache(this);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
}
