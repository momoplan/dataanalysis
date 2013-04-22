package com.ruyicai.dataanalysis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Company;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class CompanyCache {
	
	@Autowired
	private CacheService cacheService;
	
	public Company getCompany(Integer companyID) {
		String value = cacheService.get(StringUtil.join("_", "dadaanalysis", "Company", String.valueOf(companyID)));
		Company company = null;
		if(StringUtil.isEmpty(value)) {
			company = Company.findByID(companyID);
			if(null != company) {
					cacheService.set(StringUtil.join("_", "dadaanalysis", "Company", String.valueOf(companyID)), 0, company.toJson());
			}
		} else {
			company = Company.fromJsonToCompany(value);
		}
		return company;
	}
	
	public void setToMemcache(Company company) {
			cacheService.set(StringUtil.join("_", "dadaanalysis", "Company", String.valueOf(company.getCompanyID())), 0, company.toJson());
	}
}
