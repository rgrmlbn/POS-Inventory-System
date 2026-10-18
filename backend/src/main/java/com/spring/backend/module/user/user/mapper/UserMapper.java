package com.spring.backend.module.user.user.mapper;

import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.permission.entity.PermissionEntity;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserEntity toEntity(RegisterRequest request) {
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toCollection(TreeSet::new)))
                .permissions(user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toCollection(TreeSet::new)))
                .build();
    }

    public RegisterResponse toRegisterResponse(UserEntity user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}