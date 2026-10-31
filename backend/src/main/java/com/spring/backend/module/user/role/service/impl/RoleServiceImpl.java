package com.spring.backend.module.user.role.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.DuplicateRoleException;
import com.spring.backend.module.exception.user.role.RoleInUseException;
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
 * <p>Permissions are constants defined in code, so they cannot be created here;
 * they can only be assigned to roles.
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        return roleMapper.toResponse(role);
    }

    /**
     * Creates a new role with no permissions. Permissions are assigned afterwards
     * through {@link #setPermissions(Long, SetRolePermissionsRequest)}.
     *
     * @throws DuplicateRoleException if a role with the same name already exists
     */
    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String name = normalize(request.getName());

        if (roleRepository.existsByName(name)) {
            throw new DuplicateRoleException();
        }

        RoleEntity role = roleMapper.toEntity(request);
        role.setName(name);

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Updates a role's name and/or description. Null or blank names are left unchanged.
     *
     * @throws ResourceNotFoundException if the role does not exist
     * @throws DuplicateRoleException if the requested name is already in use
     */
    @Override
    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        if (request.getName() != null && !request.getName().isBlank()) {
            String name = normalize(request.getName());

            if (!name.equals(role.getName())) {
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
     * @throws ResourceNotFoundException if the role or any requested permission does not exist
     */
    @Override
    @Transactional
    public RoleResponse setPermissions(Long id, SetRolePermissionsRequest request) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        Set<String> requestedNames = request.getPermissions();
        List<PermissionEntity> found = permissionRepository.findByNameIn(requestedNames);

        // → If we found fewer than requested, some names don't exist.
        if (found.size() != requestedNames.size()) {
            throw new ResourceNotFoundException("Permission");
        }

        role.setPermissions(new HashSet<>(found));

        return roleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Deletes a role that is not assigned to any user.
     *
     * @throws ResourceNotFoundException if the role does not exist
     * @throws RoleInUseException if the role is assigned to one or more users
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        if (userRepository.existsByRolesId(id)) {
            throw new RoleInUseException();
        }

        roleRepository.delete(role);
    }

    private String normalize(String name) {
        return name.trim().toUpperCase().replace(' ', '_');
    }
}