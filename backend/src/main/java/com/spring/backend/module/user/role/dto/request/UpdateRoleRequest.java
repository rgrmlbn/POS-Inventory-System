package com.spring.backend.module.user.role.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UpdateRoleRequest {

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]*$", message = "Only letters, numbers, spaces and underscores")
    private String name;

    @Size(max = 255) private String description;
}
