package com.spring.backend.module.user.role.mapper;

import com.spring.backend.module.user.permission.entity.PermissionEntity;
import com.spring.backend.module.user.role.dto.request.CreateRoleRequest;
import com.spring.backend.module.user.role.dto.response.RoleResponse;
import com.spring.backend.module.user.role.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleEntity toEntity(CreateRoleRequest request) {
        return RoleEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public RoleResponse toResponse(RoleEntity role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toCollection(TreeSet::new)))
                .build();
    }
}