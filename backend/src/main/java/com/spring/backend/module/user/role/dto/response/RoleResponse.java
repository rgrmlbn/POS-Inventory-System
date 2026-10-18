package com.spring.backend.module.user.role.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private boolean systemRole;
    private Set<String> permissions;
}