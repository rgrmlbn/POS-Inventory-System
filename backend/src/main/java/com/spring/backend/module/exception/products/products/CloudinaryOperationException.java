package com.spring.backend.module.exception.products.products;

public class CloudinaryOperationException extends RuntimeException {
    public CloudinaryOperationException(String message) {
        super(message);
    }

    public CloudinaryOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
