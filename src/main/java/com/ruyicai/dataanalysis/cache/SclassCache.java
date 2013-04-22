package com.ruyicai.dataanalysis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class SclassCache {

	@Autowired
	private CacheService cacheService;
	
	public Sclass getSclass(Integer sclassID) {
		String value = cacheService.get(StringUtil.join("_", "dadaanalysis", "Sclass", String.valueOf(sclassID)));
		Sclass sclass = null;
		if(StringUtil.isEmpty(value)) {
			sclass = Sclass.findByID(sclassID);
			if(null != sclass) {
					cacheService.set(StringUtil.join("_", "dadaanalysis", "Sclass", String.valueOf(sclassID)), 0, sclass.toJson());
			}
		} else {
			sclass = Sclass.fromJsonToSclass(value);
		}
		return sclass;
	}
	
	public void setToMemcache(Sclass sclass) {
			cacheService.set(StringUtil.join("_", "dadaanalysis", "Sclass", String.valueOf(sclass.getSclassID())), 0, sclass.toJson());
	}
}
