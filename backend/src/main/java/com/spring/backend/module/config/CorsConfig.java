
package com.spring.backend.module.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // → Load the allowed frontend origins from the application configuration.
    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // → Create a CORS configuration to control which frontend requests are allowed.
        CorsConfiguration config = new CorsConfiguration();

        // → Allow requests only from the configured frontend origins.
        config.setAllowedOrigins(allowedOrigins);

        // → Allow the specified HTTP methods for cross-origin requests.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // → Allow the specified headers in incoming cross-origin requests.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // → Allow the frontend to read the Authorization header from responses.
        config.setExposedHeaders(List.of("Authorization"));

        // → Allow credentials, such as cookies, to be included in cross-origin requests.
        config.setAllowCredentials(true);

        // → Cache preflight responses for one hour to reduce repeated OPTIONS requests.
        config.setMaxAge(3600L);

        // → Create a configuration source that maps CORS rules to URL patterns.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // → Apply the CORS configuration to all endpoints in the application.
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
