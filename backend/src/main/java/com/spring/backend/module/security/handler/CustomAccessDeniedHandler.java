    package com.spring.backend.module.security.handler;

    import com.fasterxml.jackson.databind.ObjectMapper;

    import com.spring.backend.module.shared.response.ApiResponse;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.security.web.access.AccessDeniedHandler;
    import org.springframework.stereotype.Component;

    import java.io.IOException;
    import java.time.LocalDateTime;

    @Component
    @RequiredArgsConstructor
    public class CustomAccessDeniedHandler implements AccessDeniedHandler {

        private final ObjectMapper objectMapper;

        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {

            ApiResponse apiResponse = ApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .message("You do not have permission to access this resource.")
                    .path(request.getRequestURI())
                    .build();

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), apiResponse);
        }
    }