package com.example.captcha.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * <p>
 * 配置RedisTemplate用于存储验证码对象
 * 
 * <h3>核心功能</h3>
 * <ul>
 *   <li>配置key和value的序列化方式</li>
 *   <li>支持Java对象的JSON序列化</li>
 *   <li>支持验证码对象的存储和读取</li>
 * </ul>
 * 
 * @author PureCaptcha Team
 * @version 2.0.0 (Redis版本)
 * @since 2025-10-21
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * <p>
     * 使用Jackson序列化器，支持复杂对象的序列化
     * 
     * @param connectionFactory Redis连接工厂
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建ObjectMapper并配置类型信息
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        // 使用Jackson序列化器
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = 
            new GenericJackson2JsonRedisSerializer(objectMapper);

        // Key使用String序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value使用Jackson序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

