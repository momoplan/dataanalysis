package com.ruyicai.dataanalysis.cache;

import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class MemCachedCacheService implements CacheService, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MemcachedClient memcachedClient;

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public <T> void set(String key, T t) {
		try {
			memcachedClient.set(key, 0, t);
		} catch (Exception e) {
			logger.error("set error key:" + key + " T:" + t, e);
		}
	}

	public <T> void set(String key, Integer exp, T t) {
		if (exp == null) {
			exp = 0;
		}
		try {
			memcachedClient.set(key, exp, t);
		} catch (Exception e) {
			logger.error("set error key:" + key + "exp:" + exp + " T:" + t, e);
		}
	}

	public <T> T get(String key) {
		try {
			return (T) memcachedClient.get(key);
		} catch (Exception e) {
			logger.error("get error key:" + key, e);
			return null;
		}
	}

	public <T> void checkToSet(String key, T t) {
		T temp = null;
		try {
			temp = (T) memcachedClient.get(key);
		} catch (Exception e) {
			logger.error("checkToSet error key:" + key + " T:" + t, e);
		}
		if (temp == null) {
			set(key, t);
		}
	}

	public void delete(String key) {
		try {
			memcachedClient.delete(key);
		} catch (Exception e) {
			logger.error("delete error key:" + key, e);
		}
	}

	public void flushAll() {
		try {
			memcachedClient.flush();
		} catch (Exception e) {
			logger.error("flushAll error", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		if (memcachedClient != null) {
			memcachedClient.shutdown(2500, TimeUnit.MILLISECONDS);
		}
	}
}
