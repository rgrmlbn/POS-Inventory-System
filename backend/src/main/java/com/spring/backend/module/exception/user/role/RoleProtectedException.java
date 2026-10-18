package com.spring.backend.module.exception.user.role;

public class RoleProtectedException extends RuntimeException {
    public RoleProtectedException() {
        super("This is a built-in role and cannot be modified this way.");
    }
}
