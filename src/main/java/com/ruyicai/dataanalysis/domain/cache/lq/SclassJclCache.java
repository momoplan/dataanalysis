package com.ruyicai.dataanalysis.domain.cache.lq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.lq.SclassJcl;
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
