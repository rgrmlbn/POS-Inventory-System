
package com.spring.backend.module.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    // → Load the Redis server hostname from the application configuration.
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // → Load the Redis server port from the application configuration.
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // → Load the Redis password, defaulting to an empty string if none is configured.
    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        // → Create a RedisTemplate for storing and retrieving string keys and values.
        RedisTemplate<String, String> template = new RedisTemplate<>();

        // → Connect the template to the Redis connection factory managed by Spring.
        template.setConnectionFactory(factory);

        // → Serialize Redis keys as readable strings.
        template.setKeySerializer(new StringRedisSerializer());

        // → Serialize Redis values as readable strings.
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    public ProxyManager<String> proxyManager() {
        // → Configure the Redis server connection using the configured host and port.
        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withHost(redisHost)
                .withPort(redisPort);

        // → Add authentication credentials only when a Redis password is configured.
        if (redisPassword != null && !redisPassword.isBlank()) {
            uriBuilder.withPassword(redisPassword.toCharArray());
        }

        // → Create a Lettuce Redis client using the connection settings.
        RedisClient redisClient = RedisClient.create(uriBuilder.build());

        // → Open a Redis connection using string keys and byte-array values for Bucket4j.
        StatefulRedisConnection<String, byte[]> connection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        // → Create a Bucket4j proxy manager that stores rate-limit bucket data in Redis.
        return LettuceBasedProxyManager.builderFor(connection)
                // → Configure bucket expiration based on the time needed to refill it to capacity.
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                                Duration.ofMinutes(20)
                        )
                )
                .build();
    }
}
