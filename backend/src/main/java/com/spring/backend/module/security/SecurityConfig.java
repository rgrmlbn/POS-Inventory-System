package com.spring.backend.module.security;

import com.spring.backend.module.security.filter.JwtFilter;
import com.spring.backend.module.security.handler.CustomAccessDeniedHandler;
import com.spring.backend.module.security.handler.CustomAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.spring.backend.module.security.filter.RateLimitFilter;

/**
 * Configures the application's Spring Security settings.
 *
 * <p>Defines request authorization rules, stateless session management,
 * JWT authentication, CORS, exception handling, and password-based
 * authentication.
 *
 * <p>Enables method-level security so service and controller methods can
 * enforce permissions using annotations such as {@code @PreAuthorize}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final RateLimitFilter rateLimitFilter;

    /**
     * Configures the application's HTTP security filter chain.
     *
     * <p>Disables CSRF protection for the stateless API, permits unauthenticated
     * access to authentication and development endpoints, and requires
     * authentication for all other requests.
     *
     * @param http the HTTP security configuration
     * @return the configured security filter chain
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // → Apply the CORS configuration defined by the application.
                .cors(Customizer.withDefaults())

                // → Disable CSRF protection for this stateless API.
                .csrf(AbstractHttpConfigurer::disable)

                // → Prevent the server from creating or using HTTP sessions for authentication.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // → Define which endpoints are public and which require authentication.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // → Public authentication endpoints.
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh-token",

                                // → Development and API documentation endpoints.
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/h2-console/**",

                                // -> Public product endpoints
                                "/api/v1/products"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // → Use custom handlers for unauthenticated and unauthorized requests.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // → Allow frames from the same origin for the H2 database console.
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )

                // → Register the authentication provider and run JWT validation before
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, JwtFilter.class);

        return http.build();
    }


}