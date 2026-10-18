package com.spring.backend.module.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rate-limit.login")
@Getter
@Setter
public class LoginRateLimitProperties {
    private Limit email = new Limit();
    private Limit ip = new Limit();

    @Getter
    @Setter
    public static class Limit {
        private int capacity;
        private int refillMinutes;
    }
}