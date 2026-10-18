package com.spring.backend.module.user.user.dto.request;

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
public class AssignRolesRequest {
    @NotNull
    private Set<Long> roleIds;
}