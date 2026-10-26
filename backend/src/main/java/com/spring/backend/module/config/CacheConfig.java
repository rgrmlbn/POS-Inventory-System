package com.spring.backend.module.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.shared.response.PageResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory,
                                          ObjectMapper objectMapper) {

        var productSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ProductResponse.class);

        JavaType pageType = objectMapper.getTypeFactory()
                .constructParametricType(PageResponse.class, ProductResponse.class);
        var pageSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, pageType);

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .computePrefixWith(name -> "cache:" + name + ":")
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(base)
                .withCacheConfiguration("product", base.serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(productSerializer)))
                .withCacheConfiguration("productPages", base
                        .entryTtl(Duration.ofMinutes(2))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(pageSerializer)))
                .transactionAware()
                .build();
    }
}
