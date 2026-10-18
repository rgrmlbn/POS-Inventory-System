package com.spring.backend.module.exception.user.role;

public class RoleInUseException extends RuntimeException {
    public RoleInUseException() {
        super("This role is still assigned to one or more users.");
    }
}
