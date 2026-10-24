package com.spring.backend.module.user.user.controller;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.user.user.dto.request.AssignRolesRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for assigning roles to user accounts.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * Replaces the roles assigned to a user.
     *
     * <p>Requires {@code USER_ASSIGN_ROLE}. The caller can only grant roles whose
     * permissions they already hold.
     *
     * @param id the user's id
     * @param request the ids of the roles the user should end up with
     * @return 200 with the updated user, 403 if the grant would escalate privileges,
     *         404 if the user or a role is not found, or 409 if it would remove the last SUPER_ADMIN
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('" + Perm.USER_ASSIGN_ROLE + "')")
    public ResponseEntity<UserResponse> assignRoles(
            @PathVariable @Positive Long id,
            @RequestBody @Valid AssignRolesRequest request) {
        return ResponseEntity.ok(userRoleService.assignRolesById(id, request));
    }
}