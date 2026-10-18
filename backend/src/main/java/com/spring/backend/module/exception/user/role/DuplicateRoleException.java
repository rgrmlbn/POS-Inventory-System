package com.spring.backend.module.exception.user.role;

public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException() {
        super("A role with this name already exists.");
    }
}
