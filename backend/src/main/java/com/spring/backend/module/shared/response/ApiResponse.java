package com.spring.backend.module.shared.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApiResponse {
    private int status;
    private String message;
    private List<FieldError> errors;
    private String path;
    private LocalDateTime timestamp;

    @Getter
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}