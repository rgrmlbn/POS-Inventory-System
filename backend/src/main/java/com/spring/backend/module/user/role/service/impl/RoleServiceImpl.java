package com.spring.backend.module.user.role.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.DuplicateRoleException;
import com.spring.backend.module.exception.user.role.RoleInUseException;
import com.spring.backend.module.exception.user.role.RoleProtectedException;
import com.spring.backend.module.security.permission.RoleNames;
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
 * <p>The SUPER_ADMIN role is protected from modification or deletion. Access to
 * these operations is restricted to SUPER_ADMIN at the endpoint level.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

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
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        return roleMapper.toResponse(role);
    }

    /**
     * Creates a new role with no permissions.
     *
     * <p>The role name is normalized before checking for duplicates and saving.
     * Permissions are assigned separately through {@link #setPermissions(Long, SetRolePermissionsRequest)}.
     *
     * @param request the role name and description
     * @return the newly created role's details
     * @throws DuplicateRoleException if a role with the same name already exists
     */
    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String name = request.getName().trim().toUpperCase().replace(' ', '_');

        if (roleRepository.existsByName(name)) {
            throw new DuplicateRoleException();
        }

        RoleEntity role = roleMapper.toEntity(request);
        role.setName(name);

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Updates a role's name and/or description.
     *
     * <p>Null or blank names are left unchanged. The SUPER_ADMIN role cannot be
     * renamed, and duplicate names are rejected.
     *
     * @param id the ID of the role to update
     * @param request the fields to update
     * @return the updated role's details
     * @throws ResourceNotFoundException if the role does not exist
     * @throws RoleProtectedException if an attempt is made to rename SUPER_ADMIN
     * @throws DuplicateRoleException if the requested name is already in use
     */
    @Override
    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        if (request.getName() != null && !request.getName().isBlank()) {
            String name = request.getName().trim().toUpperCase().replace(' ', '_');

            if (!name.equals(role.getName())) {
                // → Preserve the SUPER_ADMIN name to protect built-in security rules.
                if (RoleNames.SUPER_ADMIN.equals(role.getName())) {
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
        // → Find the role.
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        // → SUPER_ADMIN's permissions can never be changed.
        if (RoleNames.SUPER_ADMIN.equals(role.getName())) {
            throw new RoleProtectedException();
        }

        Set<String> requestedNames = request.getPermissions();

        // → Fetch all requested permissions from the database in one query.
        List<PermissionEntity> foundPermissions = permissionRepository.findByNameIn(requestedNames);

        // → If we found fewer than requested, some names don't exist.
        if (foundPermissions.size() != requestedNames.size()) {
            throw new ResourceNotFoundException("Permission");
        }

        // → Replace the role's permissions and save.
        role.setPermissions(new HashSet<>(foundPermissions));

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Deletes a role if it is neither protected nor assigned to any user.
     *
     * @param id the ID of the role to delete
     * @throws ResourceNotFoundException if the role does not exist
     * @throws RoleProtectedException if the role is SUPER_ADMIN
     * @throws RoleInUseException if the role is assigned to one or more users
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        // → SUPER_ADMIN is required for the application's built-in security rules.
        if (RoleNames.SUPER_ADMIN.equals(role.getName())) {
            throw new RoleProtectedException();
        }

        // → Prevent deleting a role that is still assigned to users.
        if (userRepository.existsByRolesId(id)) {
            throw new RoleInUseException();
        }

        roleRepository.delete(role);
    }
}