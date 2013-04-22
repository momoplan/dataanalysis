package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.CompanyCache;

import flexjson.JSON;

/**
 * @author fuqiang
 * 亚赔公司
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="Company", identifierField="companyID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Company {

	@Id
	@Column(name = "CompanyID")
	private int companyID;
	
	private String name_Cn;
	
	private String name_E;

	private Double totalOdds_L;
	
	private Double totalOdds_T;

	private String name_short;

	private Integer isLetgoal;
	
	private Integer isTotalScore;

	private Integer isStandard;
	
	private Integer asianOrder;
	
	private Integer overDownOrder;
	
	private Integer standardOrder;
	
	private String msrepl_tran_version;
	
	private Integer isHalf;
	
	@Autowired
	private transient CompanyCache companyCache;
	
	@JSON(include = false)
	public CompanyCache getCompanyCache() {
		return companyCache;
	}

	public void setCompanyCache(CompanyCache companyCache) {
		this.companyCache = companyCache;
	}

	public static Company findByID(int companyID) {
        return entityManager().find(Company.class, companyID);
    }
	
	public static Company findCompany(int companyID) {
        return new Company().getCompanyCache().getCompany(companyID);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        try {
        	this.entityManager.persist(this);
        	companyCache.setToMemcache(this);
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
	
}
