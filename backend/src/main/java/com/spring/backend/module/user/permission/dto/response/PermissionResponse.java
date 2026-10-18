package com.spring.backend.module.user.permission.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private Long id;
    private String name;


}