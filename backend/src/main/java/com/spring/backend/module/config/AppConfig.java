
package com.spring.backend.module.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // → Use BCrypt to securely hash passwords before storing them in the database.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // → Get Spring Security's configured AuthenticationManager for authenticating users.
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        // → Create an ObjectMapper to convert Java objects to JSON and JSON to Java objects.
        ObjectMapper mapper = new ObjectMapper();

        // → Enable support for Java date and time classes, such as LocalDateTime and LocalDate.
        mapper.registerModule(new JavaTimeModule());

        // → Serialize dates as readable strings instead of numeric timestamps.
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
