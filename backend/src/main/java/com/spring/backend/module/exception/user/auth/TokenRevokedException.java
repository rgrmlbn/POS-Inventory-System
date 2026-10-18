package com.spring.backend.module.exception.user.auth;

public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException() {
        super("Token has been revoked");
    }
}
