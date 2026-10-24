package com.spring.backend.module.security.util;

import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Shared checks for protecting the SUPER_ADMIN role.
 */
@Component
@RequiredArgsConstructor
public class SuperAdminGuard {

    private final UserRepository userRepository;

    /**
     * Checks whether a user has the SUPER_ADMIN role.
     *
     * @param user the user to check
     * @return {@code true} if the user is a SUPER_ADMIN; otherwise {@code false}
     */
    public boolean isSuperAdmin(UserEntity user) {
        return user.getRoles().stream()
                .anyMatch(role -> RoleNames.SUPER_ADMIN.equals(role.getName()));
    }

    /**
     * Checks whether only one SUPER_ADMIN account remains.
     *
     * @return {@code true} if one or no SUPER_ADMIN accounts remain;
     *         otherwise {@code false}
     */
    public boolean isLastSuperAdmin() {
        return userRepository.countByRolesName(RoleNames.SUPER_ADMIN) <= 1;
    }
}