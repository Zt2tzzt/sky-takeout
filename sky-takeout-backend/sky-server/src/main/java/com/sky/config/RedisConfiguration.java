package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建 RedisTemplate 对象...");

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        // 设置 Redis 连接工厂对象，Spring data redis 起步依赖会创建连接工厂的 Bean 对象，并放入 IOC 容器中。
        redisTemplate.setConnectionFactory(connectionFactory);

        // 设置 Redis key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
