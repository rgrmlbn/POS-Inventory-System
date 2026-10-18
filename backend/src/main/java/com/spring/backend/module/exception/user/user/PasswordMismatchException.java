package com.spring.backend.module.exception.user.user;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Passwords don't match");
    }
}
