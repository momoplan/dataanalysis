package com.ruyicai.dataanalysis.cache;

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class MemCachedCacheService implements CacheService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	MemcachedClient memcachedClient;

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public <T> void set(String key, T t) {
		try {
			memcachedClient.set(key, 0, t);
		} catch (TimeoutException e) {
			logger.error("set error key:" + key + " T:" + t, e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			logger.error("set error key:" + key + " T:" + t, e);
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
		} catch (TimeoutException e) {
			logger.error("set error key:" + key + "exp:" + exp + " T:" + t, e);
		} catch (InterruptedException e) {
			logger.error("set error key:" + key + "exp:" + exp + " T:" + t, e);
		} catch (MemcachedException e) {
			logger.error("set error key:" + key + "exp:" + exp + " T:" + t, e);
		} catch (Exception e) {
			logger.error("set error key:" + key + "exp:" + exp + " T:" + t, e);
		}
	}

	public <T> T get(String key) {
		T t = null;
		try {
			t = memcachedClient.get(key);
		} catch (TimeoutException e) {
			logger.error("get error key:" + key, e);
		} catch (InterruptedException e) {
			logger.error("get error key:" + key, e);
		} catch (MemcachedException e) {
			logger.error("get error key:" + key, e);
		}catch (Exception e) {
			logger.error("get error key:" + key, e);
		}
		return t;
	}

	public <T> void checkToSet(String key, T t) {
		T temp = null;
		try {
			temp = memcachedClient.get(key);
		} catch (TimeoutException e) {
			logger.error("checkToSet error key:" + key + " T:" + t, e);
		} catch (InterruptedException e) {
			logger.error("checkToSet error key:" + key + " T:" + t, e);
		} catch (MemcachedException e) {
			logger.error("checkToSet error key:" + key + " T:" + t, e);
		}catch(Exception e){
			logger.error("checkToSet error key:" + key + " T:" + t, e);
		}
		if (temp == null) {
			set(key, t);
		}
	}

	public void delete(String key) {
		try {
			memcachedClient.deleteWithNoReply(key);
		} catch (InterruptedException e) {
			logger.error("delete error key:" + key, e);
		} catch (MemcachedException e) {
			logger.error("delete error key:" + key, e);
		}catch(Exception e){
			logger.error("delete error key:" + key, e);
		}
	}

	public void flushAll() {
		try {
			memcachedClient.flushAll();
		} catch (TimeoutException e) {
			logger.error("flushAll error", e);
		} catch (InterruptedException e) {
			logger.error("flushAll error", e);
		} catch (MemcachedException e) {
			logger.error("flushAll error", e);
		}catch(Exception e){
			logger.error("flushAll error", e);
		}
	}
}
