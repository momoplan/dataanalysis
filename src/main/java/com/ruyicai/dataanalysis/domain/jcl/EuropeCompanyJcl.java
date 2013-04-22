package com.ruyicai.dataanalysis.domain.jcl;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.jcl.EuropeCompanyJclCache;
import flexjson.JSON;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="europecompanyjcl", identifierField="companyId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class EuropeCompanyJcl {

	@Id
	@Column(name = "companyId")
	private int companyId;
	
	private String nameE;
	
	private String nameC;
	
	@Autowired
	private transient EuropeCompanyJclCache europeCompanyJclCache;
	
	@JSON(include = false)
	public EuropeCompanyJclCache getEuropeCompanyJclCache() {
		return europeCompanyJclCache;
	}

	public void setEuropeCompanyJclCache(EuropeCompanyJclCache europeCompanyJclCache) {
		this.europeCompanyJclCache = europeCompanyJclCache;
	}

	public static EuropeCompanyJcl findByID(int companyId) {
        return entityManager().find(EuropeCompanyJcl.class, companyId);
    }
	
	public static EuropeCompanyJcl findEuropeCompanyJcl(int companyId) {
        return new EuropeCompanyJcl().getEuropeCompanyJclCache().getEuropeCompanyJcl(companyId);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        try {
        	this.entityManager.persist(this);
        	europeCompanyJclCache.setToMemcache(this);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
}
