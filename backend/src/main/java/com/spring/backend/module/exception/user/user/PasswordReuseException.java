package com.spring.backend.module.exception.user.user;

public class PasswordReuseException extends RuntimeException {
    public PasswordReuseException() {
        super("You cannot reuse your previous password. Please choose a different password.");
    }
}
