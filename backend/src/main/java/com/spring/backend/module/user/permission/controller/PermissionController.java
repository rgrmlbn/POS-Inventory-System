package com.spring.backend.module.user.permission.controller;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.user.permission.dto.response.PermissionResponse;
import com.spring.backend.module.user.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * REST controller for retrieving available permissions.
 *
 * <p>Provides endpoints for listing permissions that administrators
 * can assign to roles when configuring role-based access control (RBAC).
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;

    /**
     * Retrieves all available permissions, sorted alphabetically by name.
     *
     * <p>Requires the ROLE_READ authority to access this endpoint.
     * Each permission is converted into a {@link PermissionResponse}
     * containing its ID and name.
     *
     * @return a {@link ResponseEntity} containing the list of permissions
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + Perm.ROLE_READ + "')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {

        // → Retrieve permissions, convert them to response DTOs, and sort by name.
        return ResponseEntity.ok(permissionRepository.findAll().stream()
                .map(p -> PermissionResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .build())
                .sorted(Comparator.comparing(PermissionResponse::getName))
                .toList());
    }
}