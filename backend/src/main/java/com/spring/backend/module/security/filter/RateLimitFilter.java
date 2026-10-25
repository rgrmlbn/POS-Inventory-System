package com.spring.backend.module.security.filter;


import com.spring.backend.module.properties.RateLimitProperties;
import com.spring.backend.module.security.util.IpExtractor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Applies Redis-backed rate limiting to every request.
 *
 * <p>Login requests use a stricter per-IP limit; all other requests use the general per-IP limit.
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    // → Change this to your real login path.
    private static final String LOGIN_PATH = "/api/v1/auth/login";

    private final ProxyManager<String> proxyManager;
    private final IpExtractor ipExtractor;
    private final RateLimitProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String ip = ipExtractor.getClientIp();

        // → Pick the stricter bucket for login, the general one for everything else.
        boolean isLogin = "POST".equalsIgnoreCase(request.getMethod())
                && LOGIN_PATH.equals(request.getRequestURI());

        String key = (isLogin ? "rate_limit:login:" : "rate_limit:general:") + ip;
        RateLimitProperties.Limit limit = isLogin ? properties.getLogin() : properties.getGeneral();

        ConsumptionProbe probe = consume(key, limit);

        if (!probe.isConsumed()) {
            // → Tell the client how many seconds to wait before retrying.
            long retryAfter = Math.max(1,
                    TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    // → Don't rate limit API documentation pages.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars");
    }

    private ConsumptionProbe consume(String key, RateLimitProperties.Limit limit) {
        Supplier<BucketConfiguration> config = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(limit.getCapacity())
                        .refillIntervally(limit.getCapacity(),
                                Duration.ofMinutes(limit.getRefillMinutes()))
                        .build())
                .build();

        return proxyManager.builder().build(key, config).tryConsumeAndReturnRemaining(1);
    }
}