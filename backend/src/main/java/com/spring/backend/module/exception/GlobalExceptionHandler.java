package com.spring.backend.module.exception;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.auth.InvalidTokenException;
import com.spring.backend.module.exception.user.auth.TokenExpiredException;
import com.spring.backend.module.exception.user.auth.TokenRevokedException;
import com.spring.backend.module.exception.user.auth.TooManyRequestsException;
import com.spring.backend.module.exception.user.user.DuplicateEmailException;
import com.spring.backend.module.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.module.exception.user.user.PasswordMismatchException;
import com.spring.backend.module.exception.user.user.PasswordReuseException;
import com.spring.backend.module.shared.response.ApiResponse;
import com.spring.backend.module.shared.response.ApiResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseBuilder apiResponseBuilder;

    // → DEFAULT

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        return apiResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // → COMMON

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return apiResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // → USER

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return apiResponseBuilder.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IncorrectCurrentPasswordException.class)
    public ResponseEntity<ApiResponse> handleIncorrectCurrentPasswordException(IncorrectCurrentPasswordException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PasswordReuseException.class)
    public ResponseEntity<ApiResponse> handlePasswordReuseException(PasswordReuseException ex) {
        return apiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // → AUTH

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenRevokedException.class)
    public ResponseEntity<ApiResponse> handleTokenRevokedException(TokenRevokedException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiResponse> handleTooManyRequestsException(TooManyRequestsException ex) {
        return apiResponseBuilder.error(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    // → BUILT-IN

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return apiResponseBuilder.error(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return apiResponseBuilder.error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return apiResponseBuilder.validationError("Validation failed",
                toFieldErrors(ex.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<ApiResponse.FieldError> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result instanceof ParameterErrors parameterErrors
                        ? toFieldErrors(parameterErrors.getFieldErrors()).stream()
                        : result.getResolvableErrors().stream()
                        .map(err -> ApiResponse.FieldError.builder()
                                .field(result.getMethodParameter().getParameterName())
                                .message(err.getDefaultMessage())
                                .build()))
                .toList();

        return apiResponseBuilder.validationError("Validation failed", errors);
    }

    private List<ApiResponse.FieldError> toFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(fe -> ApiResponse.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();
    }

}