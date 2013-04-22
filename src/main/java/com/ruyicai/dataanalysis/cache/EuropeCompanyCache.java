package com.ruyicai.dataanalysis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class EuropeCompanyCache {
	
	@Autowired
	private CacheService cacheService;
	
	public EuropeCompany getEuropeCompany(Integer companyID) {
		String value = cacheService.get(StringUtil.join("_", "dadaanalysis", "EuropeCompany", String.valueOf(companyID)));
		EuropeCompany company = null;
		if(StringUtil.isEmpty(value)) {
			company = EuropeCompany.findByID(companyID);
			if(null != company) {
					cacheService.set(StringUtil.join("_", "dadaanalysis", "EuropeCompany", String.valueOf(companyID)), 0, company.toJson());
			}
		} else {
			company = EuropeCompany.fromJsonToEuropeCompany(value);
		}
		return company;
	}
	
	public void setToMemcache(EuropeCompany company) {
			cacheService.set(StringUtil.join("_", "dadaanalysis", "EuropeCompany", String.valueOf(company.getCompanyID())), 0, company.toJson());
	}
}
