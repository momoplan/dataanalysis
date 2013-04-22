package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.EuropeCompanyCache;

import flexjson.JSON;

/**
 * @author fuqiang
 * 欧赔公司
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="EuropeCompany", identifierField="companyID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class EuropeCompany {

	@Id
	@Column(name = "CompanyID")
	private int companyID;
	
	private String name_Cn;
	
	private String name_E;
	
	private Integer isPrimary;
	
	private Integer isExchange;
	
	@Autowired
	private transient EuropeCompanyCache europeCompanyCache;
	
	@JSON(include = false)
	public EuropeCompanyCache getEuropeCompanyCache() {
		return europeCompanyCache;
	}

	public void setEuropeCompanyCache(EuropeCompanyCache europeCompanyCache) {
		this.europeCompanyCache = europeCompanyCache;
	}

	public static EuropeCompany findByID(int companyID) {
        return entityManager().find(EuropeCompany.class, companyID);
    }
	
	public static EuropeCompany findEuropeCompany(int companyID) {
        return new EuropeCompany().getEuropeCompanyCache().getEuropeCompany(companyID);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        try {
        	this.entityManager.persist(this);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
}
