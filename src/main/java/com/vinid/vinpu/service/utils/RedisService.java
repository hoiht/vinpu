package com.vinid.vinpu.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinid.vinpu.web.rest.vm.RedisUser;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RedisService {
	private static final Logger log = LoggerFactory.getLogger(RedisService.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private final String KEY_USER = "KEY_";
	
	public RedisService() {
	}
	
	public void saveRedisUser(RedisUser redisUser) {
		try {
			String userKey = KEY_USER + redisUser.getLogin();
			redisTemplate.opsForValue().set(userKey, mapper.writeValueAsString(redisUser));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RedisUser getRedisUserByToken(String redisKey) {
		RedisUser redisUser = new RedisUser(); 
		try {
			String userKey = KEY_USER + redisKey;
			Object jsonOutput = redisTemplate.opsForValue().get(userKey);
			redisUser = mapper.readValue(jsonOutput.toString(), RedisUser.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return redisUser;
	}
}
