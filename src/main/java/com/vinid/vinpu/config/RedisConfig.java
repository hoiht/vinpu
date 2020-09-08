package com.vinid.vinpu.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.redisson.spring.data.connection.RedissonConnectionFactory;

@Configuration
public class RedisConfig {
    public static final String CONFIG_PREFIX = "hibernate.cache.redisson.";
    
    public static final String REDISSON_CONFIG_PATH = CONFIG_PREFIX + "config";
    
	@Bean
    public RedissonConnectionFactory redissonConnectionFactory() {
        return new RedissonConnectionFactory();
    }
	
	
	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		Jackson2JsonRedisSerializer jrs = new Jackson2JsonRedisSerializer(Object.class);

		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
//		JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
		template.setConnectionFactory(redissonConnectionFactory());
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setValueSerializer(jrs);
		template.setHashValueSerializer(jrs);
		template.setEnableTransactionSupport(true);
		template.afterPropertiesSet();
		return template;
	}
}
