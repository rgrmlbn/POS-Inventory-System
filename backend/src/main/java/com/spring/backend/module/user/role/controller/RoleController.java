package com.spring.backend.module.user.role.controller;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.user.role.dto.request.CreateRoleRequest;
import com.spring.backend.module.user.role.dto.request.SetRolePermissionsRequest;
import com.spring.backend.module.user.role.dto.request.UpdateRoleRequest;
import com.spring.backend.module.user.role.dto.response.RoleResponse;
import com.spring.backend.module.user.role.service.interfaces.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for role management.
 *
 * <p>All endpoints require the appropriate permission to access or modify roles.
 * Creating, updating, and deleting roles, as well as managing role permissions,
 * are handled by the {@link RoleService}.
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    /**
     * Lists all roles with their permissions.
     *
     * <p>Requires {@code ROLE_READ}.
     *
     * @return 200 with every role
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + Perm.ROLE_READ + "')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    /**
     * Returns a single role by ID, including its permissions.
     *
     * <p>Requires {@code ROLE_READ}.
     *
     * @param id the role's ID
     * @return 200 with the role, or 404 if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.ROLE_READ + "')")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    /**
     * Creates a new role with an optional set of permissions.
     *
     * <p>Requires {@code ROLE_CREATE}. The requested permissions must be
     * within the caller's authorized privileges.
     *
     * @param request the role name, description, and optional permissions
     * @return 201 with the created role, 403 if granting permissions would escalate
     *         privileges, 404 if a requested permission is not found,
     *         or 409 if the role name already exists
     */
    @PostMapping
    @PreAuthorize("hasAuthority('" + Perm.ROLE_CREATE + "')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody @Valid CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
    }

    /**
     * Updates a role's name and/or description.
     *
     * <p>Requires {@code ROLE_UPDATE}. System role names cannot be changed.
     *
     * @param id the ID of the role to update
     * @param request the fields to change
     * @return 200 with the updated role, 404 if not found, or 409 if the role
     *         is protected or the requested name is already taken
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.ROLE_UPDATE + "')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    /**
     * Replaces the permissions assigned to a role.
     *
     * <p>Requires {@code ROLE_UPDATE}. The caller can only grant permissions
     * within their authorized privileges, and the SUPER_ADMIN role is protected
     * from having its permissions replaced.
     *
     * @param id the ID of the role whose permissions will be replaced
     * @param request the permission names to assign to the role
     * @return 200 with the updated role, 403 if granting permissions would escalate
     *         privileges, 404 if the role or a requested permission is not found,
     *         or 409 if the role is protected
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('" + Perm.ROLE_UPDATE + "')")
    public ResponseEntity<RoleResponse> setPermissions(
            @PathVariable @Positive Long id,
            @RequestBody @Valid SetRolePermissionsRequest request) {
        return ResponseEntity.ok(roleService.setPermissions(id, request));
    }

    /**
     * Deletes a role that is not protected or assigned to any user.
     *
     * <p>Requires {@code ROLE_DELETE}.
     *
     * @param id the ID of the role to delete
     * @return 204 on success, 404 if the role is not found, or 409 if the role
     *         is protected or still assigned to one or more users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.ROLE_DELETE + "')")
    public ResponseEntity<Void> deleteRole(@PathVariable @Positive Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}