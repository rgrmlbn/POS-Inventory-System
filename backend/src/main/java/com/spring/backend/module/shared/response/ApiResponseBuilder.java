package com.spring.backend.module.shared.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ApiResponseBuilder {

    public ResponseEntity<ApiResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(build(status, message, null));
    }

    public ResponseEntity<ApiResponse> validationError(String message, List<ApiResponse.FieldError> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(HttpStatus.BAD_REQUEST, message, errors));
    }

    private String getCurrentPath() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getRequestURI();
        }
        return null;
    }

    private ApiResponse build(HttpStatus status, String message, List<ApiResponse.FieldError> errors) {
        return ApiResponse.builder()
                .status(status.value())
                .message(message)
                .errors(errors)
                .path(getCurrentPath())
                .timestamp(LocalDateTime.now())
                .build();
    }
}