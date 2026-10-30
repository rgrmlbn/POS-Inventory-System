package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.LastSuperAdminException;
import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.security.util.SuperAdminGuard;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.role.repository.RoleRepository;
import com.spring.backend.module.user.user.dto.request.AssignRolesRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.interfaces.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements role assignment for user accounts, with privilege escalation
 * protection.
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final SuperAdminGuard superAdminGuard;

    /**
     * Replaces the roles assigned to a user.
     *
     * <p>The caller can only grant roles whose permissions they already hold,
     * and the last SUPER_ADMIN cannot lose that role.
     *
     * @param id the ID of the user whose roles will be replaced
     * @param request the requested role IDs
     * @return the updated user's details and roles
     * @throws ResourceNotFoundException if the user or any requested role does not exist
     * @throws LastSuperAdminException if the operation would remove the last SUPER_ADMIN
     * @throws AccessDeniedException if the caller attempts to escalate their privileges
     */
    @Override
    @Transactional
    public UserResponse assignRolesById(Long id, AssignRolesRequest request) {
        UserEntity user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        Set<Long> requestedIds = request.getRoleIds();
        List<RoleEntity> roles = roleRepository.findAllById(requestedIds);

        // → findAllById silently skips unknown ids, so compare sizes to detect them
        if (roles.size() != requestedIds.size()) {
            throw new ResourceNotFoundException("Role");
        }

        boolean losesSuperAdmin = superAdminGuard.isSuperAdmin(user)
                && roles.stream().noneMatch(role -> RoleNames.SUPER_ADMIN.equals(role.getName()));

        if (losesSuperAdmin && superAdminGuard.isLastSuperAdmin()) {
            throw new LastSuperAdminException();
        }

        user.setRoles(new HashSet<>(roles));

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}