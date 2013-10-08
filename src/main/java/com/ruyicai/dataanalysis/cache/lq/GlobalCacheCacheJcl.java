package com.ruyicai.dataanalysis.cache.lq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.cache.CacheService;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class GlobalCacheCacheJcl {

	@Autowired
	private CacheService cacheService;
	
	public GlobalCacheJcl getGlobalCacheJcl(String id) {
		String value = cacheService.get(id);
		GlobalCacheJcl globalCacheJcl = null;
		if(StringUtil.isEmpty(value)) {
			globalCacheJcl = GlobalCacheJcl.findById(id);
			if(null != globalCacheJcl) {
					cacheService.set(id, 0, globalCacheJcl.toJson());
			}
		} else {
			globalCacheJcl = GlobalCacheJcl.fromJsonToGlobalCache(value);
		}
		return globalCacheJcl;
	}
	
	public void setToMemcache(GlobalCacheJcl globalCacheJcl) {
			cacheService.set(globalCacheJcl.getId(), globalCacheJcl.toJson());
	}
	
}
