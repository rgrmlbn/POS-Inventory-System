package com.spring.backend.module.user.auth.service.impl;

import com.spring.backend.module.config.LoginRateLimitProperties;
import com.spring.backend.module.exception.user.auth.TooManyRequestsException;
import com.spring.backend.module.security.util.IpExtractor;
import com.spring.backend.module.user.auth.service.interfaces.LoginRateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Implementation of the login rate-limiting service.
 *
 * <p>Limits login attempts based on the user's email address and client IP
 * address to help protect the authentication endpoint against brute-force
 * attacks.
 *
 * <p>Uses Bucket4j with a distributed {@link ProxyManager} so rate-limit
 * buckets can be shared across application instances when backed by a
 * distributed storage provider.
 */
@Service
@RequiredArgsConstructor
public class LoginRateLimiterServiceImpl implements LoginRateLimiterService {

    private final ProxyManager<String> proxyManager;
    private final IpExtractor ipExtractor;
    private final LoginRateLimitProperties loginRateLimitProperties;

    /**
     * Checks whether the login attempt is allowed by both rate limits.
     *
     * @param email the email address used in the login attempt
     * @throws TooManyRequestsException if either the email or IP rate limit is exceeded
     */
    @Override
    public void checkLimits(String email) {

        // → Apply separate limits to the email address and the client's IP address.
        checkEmailLimit(email);
        checkIpLimit(ipExtractor.getClientIp());
    }

    // HELPER

    /**
     * Enforces the configured login-attempt limit for an email address.
     *
     * @param email the email address being used to log in
     */
    private void checkEmailLimit(String email) {

        // → Retrieve the capacity and refill interval configured for email-based limits.
        var cfg = loginRateLimitProperties.getEmail();

        // → Use the email address to maintain a separate bucket for each account.
        check("rate_limit:email:" + email, cfg.getCapacity(),
                Duration.ofMinutes(cfg.getRefillMinutes()));
    }

    /**
     * Enforces the configured login-attempt limit for a client IP address.
     *
     * @param ip the IP address making the login attempt
     */
    private void checkIpLimit(String ip) {

        // → Retrieve the capacity and refill interval configured for IP-based limits.
        var cfg = loginRateLimitProperties.getIp();

        // → Use the IP address to maintain a separate bucket for each client.
        check("rate_limit:ip:" + ip, cfg.getCapacity(),
                Duration.ofMinutes(cfg.getRefillMinutes()));
    }

    /**
     * Attempts to consume one token from the rate-limit bucket identified by the key.
     *
     * <p>The bucket is configured with the specified token capacity and refill
     * window. If no token is available, the request is rejected.
     *
     * @param key the unique key identifying the rate-limit bucket
     * @param capacity the maximum number of tokens available in the bucket
     * @param window the interval over which tokens are replenished
     * @throws TooManyRequestsException if the bucket has no available token
     */
    private void check(String key, int capacity, Duration window) {

        // → Define the bucket's token capacity and refill policy.
        Supplier<BucketConfiguration> config = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, window)
                        .build())
                .build();

        // → Retrieve or create the distributed bucket associated with this key.
        var bucket = proxyManager.builder().build(key, config);

        // → Consume one token for the attempt; reject the request when the limit is reached.
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
    }
}