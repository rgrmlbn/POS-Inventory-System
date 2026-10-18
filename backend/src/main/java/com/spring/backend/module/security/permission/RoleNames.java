package com.spring.backend.module.security.permission;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class RoleNames {

    /** Built-in. Always holds every permission. Cannot be edited or deleted. */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    /** Built-in default role given to every newly registered user. Cannot be renamed or deleted. */
    public static final String USER = "USER";

    public static final String MANAGER = "MANAGER";

    public static final String CASHIER = "CASHIER";
}
