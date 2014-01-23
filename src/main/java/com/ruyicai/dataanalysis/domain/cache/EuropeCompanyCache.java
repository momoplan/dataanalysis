package com.ruyicai.dataanalysis.domain.cache;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.EuropeCompany;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class EuropeCompanyCache {
	
	@Autowired
	private CacheService cacheService;
	
	public EuropeCompany getEuropeCompany(Integer companyID) {
		String key = StringUtil.join("_", "dadaanalysis", "EuropeCompany", String.valueOf(companyID));
		String value = cacheService.get(key);
		EuropeCompany company = null;
		if(StringUtil.isEmpty(value)) {
			company = EuropeCompany.findByID(companyID);
			if(null != company) {
				cacheService.set(key, 0, company.toJson());
			}
		} else {
			company = EuropeCompany.fromJsonToEuropeCompany(value);
		}
		return company;
	}
	
	/*public void setToMemcache(EuropeCompany company) {
		cacheService.set(StringUtil.join("_", "dadaanalysis", "EuropeCompany", String.valueOf(company.getCompanyID())), 0, company.toJson());
	}*/
	
	public List<Integer> getPrimaryCompanyIds() {
		String key = StringUtil.join("_", "dadaanalysis", "EuropeCompany", "Primary");
		List<Integer> value = cacheService.get(key);
		if (value==null) {
			value = getPrimaryIds();
			if (value!=null&&value.size()>0) {
				cacheService.set(key, value);
			}
		}
		return value;
	}
	
	private List<Integer> getPrimaryIds() {
		List<Integer> result = new ArrayList<Integer>();
		List<EuropeCompany> list = EuropeCompany.findByPrimary();
		if (list!=null&&list.size()>0) {
			for (EuropeCompany europeCompany : list) {
				result.add(europeCompany.getCompanyID());
			}
		}
		return result;
	}
	
}
