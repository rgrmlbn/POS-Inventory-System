package com.spring.backend.module.security.permission;

import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Single source of truth for every permission the app knows about.
 * Add new permissions here.
 */
@NoArgsConstructor
public final class Perm {

    public static final String USER_READ = "USER_READ";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_ASSIGN_ROLE = "USER_ASSIGN_ROLE";

    public static final String ROLE_READ = "ROLE_READ";
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_UPDATE = "ROLE_UPDATE";
    public static final String ROLE_DELETE = "ROLE_DELETE";

    public static final String CATEGORY_READ = "CATEGORY_READ";
    public static final String CATEGORY_CREATE = "CATEGORY_CREATE";
    public static final String CATEGORY_UPDATE = "CATEGORY_UPDATE";
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";

    public static final String PRODUCT_READ = "PRODUCT_READ";
    public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
    public static final String PRODUCT_UPDATE = "PRODUCT_UPDATE";
    public static final String PRODUCT_DELETE = "PRODUCT_READ";



    /**
     * Returns all permission names for the startup seeder.
     */
    public static List<String> all() {
        return List.of(
                USER_READ,
                USER_UPDATE,
                USER_DELETE,
                USER_ASSIGN_ROLE,
                ROLE_READ,
                ROLE_CREATE,
                ROLE_UPDATE,
                ROLE_DELETE,
                CATEGORY_READ,
                CATEGORY_CREATE,
                CATEGORY_UPDATE,
                CATEGORY_DELETE,
                PRODUCT_READ,
                PRODUCT_CREATE,
                PRODUCT_UPDATE,
                PRODUCT_DELETE
        );
    }
}