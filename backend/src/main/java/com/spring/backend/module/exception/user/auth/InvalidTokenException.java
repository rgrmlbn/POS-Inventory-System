package com.spring.backend.module.exception.user.auth;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException( ) {
        super("Invalid token");
    }
}
