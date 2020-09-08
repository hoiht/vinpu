package com.vinid.vinpu.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class RedisService {
	private static final Logger log = LoggerFactory.getLogger(RedisService.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();
}
