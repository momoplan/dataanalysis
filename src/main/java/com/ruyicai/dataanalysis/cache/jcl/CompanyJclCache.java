package com.ruyicai.dataanalysis.cache.jcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.jcl.CompanyJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class CompanyJclCache {
	
	@Autowired
	private CacheService cacheService;
	
	public CompanyJcl getCompanyJcl(Integer companyId) {
		String id = StringUtil.join("_", "dadaanalysis", "CompanyJcl", String.valueOf(companyId));
		String value = cacheService.get(id);
		CompanyJcl companyJcl = null;
		if(StringUtil.isEmpty(value)) {
			companyJcl = CompanyJcl.findByID(companyId);
			if(null != companyJcl) {
				cacheService.set(id, 0, companyJcl.toJson());
			}
		} else {
			companyJcl = CompanyJcl.fromJsonToCompanyJcl(value);
		}
		return companyJcl;
	}
	
	public void setToMemcache(CompanyJcl companyJcl) {
		String id = StringUtil.join("_", "dadaanalysis", "CompanyJcl", String.valueOf(companyJcl.getCompanyId()));
		cacheService.set(id, 0, companyJcl.toJson());
	}
}
