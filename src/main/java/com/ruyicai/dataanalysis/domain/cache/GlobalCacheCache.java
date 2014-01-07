package com.ruyicai.dataanalysis.domain.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class GlobalCacheCache {

	@Autowired
	private CacheService cacheService;
	
	public GlobalCache getGlobalCache(String id) {
		String value = cacheService.get(id);
		GlobalCache globalCache = null;
		if(StringUtil.isEmpty(value)) {
			globalCache = GlobalCache.findById(id);
			if(null != globalCache) {
					cacheService.set(id, 0, globalCache.toJson());
			}
		} else {
			globalCache = GlobalCache.fromJsonToGlobalCache(value);
		}
		return globalCache;
	}
	
	public void setToMemcache(GlobalCache globalCache) {
			cacheService.set(globalCache.getId(), globalCache.toJson());
	}
}
