package com.spring.backend.module.properties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    // → Applies to every endpoint except login.
    private Limit general = new Limit(100, 1);

    // → Stricter limit that applies only to the login endpoint.
    private Limit login = new Limit(5, 15);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Limit {
        private int capacity;
        private int refillMinutes;
    }
}
