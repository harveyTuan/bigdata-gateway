package com.hncy58.bigdata.gateway.service.imlp;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hncy58.bigdata.gateway.service.AbstractCacheService;
import com.hncy58.bigdata.gateway.service.TokenService;
import com.hncy58.bigdata.gateway.util.SnowflakeIdWorker;

/**
 * Token服务
 * 
 * @author tdz
 * @company hncy58 长银五八
 * @website http://www.hncy58.com
 * @version 1.0
 * @date 2018年8月14日 下午1:23:02
 */
@Service
public class TokenServiceImpl extends AbstractCacheService<Object> implements TokenService {

	@Value("${cache.token.key:token}")
	protected String cacheKey;

	@Value("${cache.token.key:-1}")
	protected long cacheExpire;

	@Autowired
	private SnowflakeIdWorker idWorker;

	@Override
	protected String getCacheKey() {
		return cacheKey;
	}

	@Override
	protected long getCacheExpire() {
		return cacheExpire;
	}

	@Override
	public String generateToken(int userId) {
		long tokenId = idWorker.nextId();
		String token = cacheKey + "#" + userId + "#" + tokenId;
		put(token, token, token, cacheExpire);
		putToken(userId, token);
		return token;
	}

	@Override
	public Object getCacheFromToken(String token, String key) {
		// 更新缓存超时时间
		if (cacheExpire != -1) {
			redisTemplate.expire(token, cacheExpire, TimeUnit.SECONDS);
		}
		return get(token, key);
	}

	@Override
	public void putCacheByToken(String token, String key, Object t) {
		put(token, key, t, cacheExpire);
	}

	@Override
	public void removeToken(String token) {
		if (StringUtils.isEmpty(token) || !token.contains("#"))
			return;
		String userId = token.trim().split("#")[1];
		remove(cacheKey + ":all", userId);
		redisTemplate.delete(token);
	}

	@Override
	public boolean validateToken(String token) {
		return redisTemplate.hasKey(token);
	}

	@Override
	public boolean exists(int userId) {
		return hashOperations.hasKey(cacheKey + ":all", userId + "");
	}

	@Override
	public String getToken(int userId) {
		Object t = get(cacheKey + ":all", userId + "");
		if (t != null)
			return t.toString();
		return null;
	}

	@Override
	public void putToken(int userId, String token) {
		if (userId > 0)
			put(cacheKey + ":all", userId + "", token, cacheExpire);
	}
}
