package com.spring.backend.module.user.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SetRolePermissionsRequest {
    @NotNull
    @NotBlank
    private Set<String> permissions;
}
