package com.spring.backend.module.exception.user.auth;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Too many requests. Please try again later.");
    }
}
