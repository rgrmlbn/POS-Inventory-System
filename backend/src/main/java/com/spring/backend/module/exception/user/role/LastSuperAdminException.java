package com.spring.backend.module.exception.user.role;

public class LastSuperAdminException extends RuntimeException {
    public LastSuperAdminException() {
        super("At least one SUPER_ADMIN user must remain.");
    }
}
