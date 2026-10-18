package com.spring.backend.module.user.token.service.impl;

import com.spring.backend.module.security.util.JwtUtil;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Implementation of the token blacklist service.
 *
 * <p>Uses Redis to store blacklisted JWTs until their expiration time,
 * preventing revoked or logged-out tokens from being used again.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    // → Prefix used to distinguish blacklisted tokens in Redis.
    private static final String PREFIX = "blacklist:";

    /**
     * Adds a JWT to the blacklist until it expires.
     *
     * @param token the JWT to blacklist
     */
    @Override
    public void blacklist(String token) {
        // → Extract the token's expiration time to determine how long it should remain in Redis.
        Instant expiry = jwtUtil.extractExpiration(token);
        Duration ttl = Duration.between(Instant.now(), expiry);

        // → Store only tokens that have not yet expired, using their remaining lifetime as the TTL.
        if (!ttl.isNegative()) {
            redisTemplate.opsForValue().set(PREFIX + token, "true", ttl);
        }
    }

    /**
     * Checks whether a JWT exists in the Redis blacklist.
     *
     * @param token the JWT to check
     * @return {@code true} if the token is blacklisted; otherwise, {@code false}
     */
    @Override
    public boolean isBlacklisted(String token) {
        // → Check whether Redis contains an entry for this token's blacklist key.
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}