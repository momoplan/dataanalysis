package com.ruyicai.dataanalysis.cache.jcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.jcl.SclassJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class SclassJclCache {

	@Autowired
	private CacheService cacheService;
	
	public SclassJcl getSclassJcl(Integer sclassID) {
		String id = StringUtil.join("_", "dadaanalysis", "SclassJcl", String.valueOf(sclassID));
		String value = cacheService.get(id);
		SclassJcl sclassJcl = null;
		if(StringUtil.isEmpty(value)) {
			sclassJcl = SclassJcl.findByID(sclassID);
			if(null != sclassJcl) {
				cacheService.set(id, 0, sclassJcl.toJson());
			}
		} else {
			sclassJcl = SclassJcl.fromJsonToSclassJcl(value);
		}
		return sclassJcl;
	}
	
	public void setToMemcache(SclassJcl sclassJcl) {
		String id = StringUtil.join("_", "dadaanalysis", "SclassJcl", String.valueOf(sclassJcl.getSclassId()));
		cacheService.set(id, 0, sclassJcl.toJson());
	}
	
}
