package com.spring.backend.module.user.role.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.DuplicateRoleException;
import com.spring.backend.module.exception.user.role.RoleInUseException;
import com.spring.backend.module.exception.user.role.RoleProtectedException;
import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.security.util.EscalationGuard;
import com.spring.backend.module.user.permission.entity.PermissionEntity;
import com.spring.backend.module.user.permission.repository.PermissionRepository;
import com.spring.backend.module.user.role.dto.request.CreateRoleRequest;
import com.spring.backend.module.user.role.dto.request.SetRolePermissionsRequest;
import com.spring.backend.module.user.role.dto.request.UpdateRoleRequest;
import com.spring.backend.module.user.role.dto.response.RoleResponse;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.role.mapper.RoleMapper;
import com.spring.backend.module.user.role.repository.RoleRepository;
import com.spring.backend.module.user.role.service.interfaces.RoleService;
import com.spring.backend.module.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements business operations for role management.
 *
 * <p>Supports creating, retrieving, updating, and deleting roles, as well as
 * assigning permissions to roles.
 *
 * <p>System roles are protected from modification or deletion where required.
 * Permission assignments are also checked against privilege escalation rules
 * to prevent callers from granting permissions they are not authorized to grant.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final EscalationGuard escalationGuard;

    /**
     * Returns all roles with their mapped response details.
     *
     * @return a list of all roles
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    /**
     * Returns a single role by ID, including its permissions.
     *
     * @param id the ID of the role to retrieve
     * @return the role's details and permissions
     * @throws ResourceNotFoundException if the role does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        return roleMapper.toResponse(find(id));
    }

    /**
     * Creates a new role with an optional set of permissions.
     *
     * <p>The role name is normalized before checking for duplicates and saving.
     * Requested permissions are validated against privilege escalation rules
     * before they are assigned to the role.
     *
     * @param request the role name, description, and optional permission names
     * @return the newly created role's details
     * @throws DuplicateRoleException if a role with the same name already exists
     * @throws ResourceNotFoundException if any requested permission does not exist
     */
    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String name = normalize(request.getName());

        if (roleRepository.existsByName(name)) {
            throw new DuplicateRoleException();
        }

        RoleEntity role = RoleEntity.builder()
                .name(name)
                .description(request.getDescription())
                .build();

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            Set<PermissionEntity> permissions = resolvePermissions(request.getPermissions());

            // → Prevent assigning permissions that the caller is not authorized to grant.
            escalationGuard.assertCanGrant(request.getPermissions());

            role.setPermissions(permissions);
        }

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Updates a role's name and/or description.
     *
     * <p>Null or blank names are left unchanged. System role names cannot be
     * changed, and duplicate names are rejected.
     *
     * @param id the ID of the role to update
     * @param request the fields to update
     * @return the updated role's details
     * @throws ResourceNotFoundException if the role does not exist
     * @throws RoleProtectedException if an attempt is made to rename a system role
     * @throws DuplicateRoleException if the requested name is already in use
     */
    @Override
    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        RoleEntity role = find(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            String name = normalize(request.getName());

            if (!name.equals(role.getName())) {
                // → Preserve system role names to protect built-in security rules.
                if (role.isSystemRole()) {
                    throw new RoleProtectedException();
                }

                if (roleRepository.existsByName(name)) {
                    throw new DuplicateRoleException();
                }

                role.setName(name);
            }
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Replaces the permissions assigned to a role.
     *
     * <p>The SUPER_ADMIN role cannot have its permissions replaced, ensuring
     * that the system retains its highest-privilege role.
     *
     * @param id the ID of the role whose permissions will be replaced
     * @param request the requested permission names
     * @return the updated role's details
     * @throws ResourceNotFoundException if the role or any requested permission does not exist
     * @throws RoleProtectedException if the role is SUPER_ADMIN
     */
    @Override
    @Transactional
    public RoleResponse setPermissions(Long id, SetRolePermissionsRequest request) {
        RoleEntity role = find(id);

        // → SUPER_ADMIN must retain its permissions to prevent the system from being locked out.
        if (RoleNames.SUPER_ADMIN.equals(role.getName())) {
            throw new RoleProtectedException();
        }

        Set<PermissionEntity> permissions = resolvePermissions(request.getPermissions());

        // → Prevent granting permissions beyond the caller's authorized privileges.
        escalationGuard.assertCanGrant(request.getPermissions());

        role.setPermissions(permissions);

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Deletes a role if it is neither protected nor assigned to any user.
     *
     * @param id the ID of the role to delete
     * @throws ResourceNotFoundException if the role does not exist
     * @throws RoleProtectedException if the role is a system role
     * @throws RoleInUseException if the role is assigned to one or more users
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity role = find(id);

        // → System roles are required for the application's built-in security rules.
        if (role.isSystemRole()) {
            throw new RoleProtectedException();
        }

        // → Prevent deleting a role that is still assigned to users.
        if (userRepository.existsByRolesId(id)) {
            throw new RoleInUseException();
        }

        roleRepository.delete(role);
    }

    // HELPER

    /**
     * Finds a role by ID with its permissions loaded.
     *
     * @param id the ID of the role to find
     * @return the role with its permissions
     * @throws ResourceNotFoundException if the role does not exist
     */
    private RoleEntity find(Long id) {
        return roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));
    }

    /**
     * Resolves permission names to their corresponding entities.
     *
     * <p>The repository may return fewer permissions than requested when some
     * names do not exist, so the result size is checked before returning.
     *
     * @param names the permission names to resolve
     * @return the set of matching permission entities
     * @throws ResourceNotFoundException if any requested permission does not exist
     */
    private Set<PermissionEntity> resolvePermissions(Set<String> names) {
        Set<PermissionEntity> found = new HashSet<>(permissionRepository.findAllByNameIn(names));

        // → Detect unknown permission names because findAllByNameIn skips unmatched names.
        if (found.size() != names.size()) {
            throw new ResourceNotFoundException("Permission");
        }

        return found;
    }

    /**
     * Normalizes a role name by trimming whitespace, converting it to uppercase,
     * and replacing spaces with underscores.
     *
     * @param name the role name to normalize
     * @return the normalized role name
     */
    private String normalize(String name) {
        return name.trim().toUpperCase().replace(' ', '_');
    }
}