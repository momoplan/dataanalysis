package com.ruyicai.dataanalysis.cache.jcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.jcl.EuropeCompanyJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class EuropeCompanyJclCache {

	@Autowired
	private CacheService cacheService;

	public EuropeCompanyJcl getEuropeCompanyJcl(Integer companyId) {
		String id = StringUtil.join("_", "dadaanalysis", "EuropeCompanyJcl", String.valueOf(companyId));
		String value = cacheService.get(id);
		EuropeCompanyJcl europeCompanyJcl = null;
		if (StringUtil.isEmpty(value)) {
			europeCompanyJcl = EuropeCompanyJcl.findByID(companyId);
			if (null != europeCompanyJcl) {
				cacheService.set(id, 0, europeCompanyJcl.toJson());
			}
		} else {
			europeCompanyJcl = EuropeCompanyJcl.fromJsonToEuropeCompanyJcl(value);
		}
		return europeCompanyJcl;
	}

	public void setToMemcache(EuropeCompanyJcl europeCompanyJcl) {
		String id = StringUtil.join("_", "dadaanalysis", "EuropeCompanyJcl", String.valueOf(europeCompanyJcl.getCompanyId()));
		cacheService.set(id, 0, europeCompanyJcl.toJson());
	}
	
}
