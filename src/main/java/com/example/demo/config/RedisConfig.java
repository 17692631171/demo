package com.example.demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 创建 RedisTemplate Bean
     * 用于操作 Redis
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置序列化器
        // Key 使用 String 序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value 使用 JSON 序列化器
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Hash Key 使用 String 序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value 使用 JSON 序列化器
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置默认序列化器
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        // 初始化
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 创建 StringRedisTemplate Bean
     * 用于操作字符串类型的 Redis 数据
     */
    @Bean
    public org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        return new org.springframework.data.redis.core.StringRedisTemplate(redisConnectionFactory);
    }
}