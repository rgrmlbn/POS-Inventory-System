
package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.LastSuperAdminException;
import com.spring.backend.module.exception.user.user.DuplicateEmailException;
import com.spring.backend.module.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.module.exception.user.user.PasswordMismatchException;
import com.spring.backend.module.exception.user.user.PasswordReuseException;
import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.security.util.CurrentUser;
import com.spring.backend.module.security.util.EscalationGuard;
import com.spring.backend.module.user.user.dto.request.AssignRolesRequest;
import com.spring.backend.module.user.permission.entity.PermissionEntity;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.role.repository.RoleRepository;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements business operations for user accounts.
 *
 * <p>Methods ending in {@code ById} operate on a specified user and are expected
 * to be protected by permission checks at the controller. The "me" methods use
 * the authenticated user's identity from the security context.
 *
 * <p>Operations involving role assignments enforce privilege escalation
 * protection, while account deletion and password changes revoke refresh tokens
 * to prevent affected users from renewing their sessions.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EscalationGuard escalationGuard;

    /**
     * Returns every user with their roles.
     *
     * @return a list of users with their roles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<UserEntity> user = userRepository.findAll(pageable);

        return user.map(UserEntity -> userMapper.toResponse(UserEntity));
    }

    /**
     * Returns a single user with their roles.
     *
     * @param id the ID of the user to retrieve
     * @return the user's details and roles
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findWithRoles(id));
    }

    /**
     * Updates a user's name and/or email. Null or blank fields are left unchanged.
     *
     * @param id the ID of the user to update
     * @param update the fields to update
     * @return the updated user's details
     * @throws ResourceNotFoundException if the user does not exist
     * @throws DuplicateEmailException if the email is already taken
     */
    @Override
    @Transactional
    public UserResponse updateUserById(Long id, UpdateUserRequest update) {
        UserEntity user = findWithRoles(id);

        if (update.getName() != null && !update.getName().isBlank()) {
            user.setName(update.getName());
        }

        if (update.getEmail() != null && !update.getEmail().isBlank()) {
            // → Only hit the database when the email actually changes
            if (!user.getEmail().equals(update.getEmail())
                    && userRepository.existsByEmail(update.getEmail())) {
                throw new DuplicateEmailException();
            }
            user.setEmail(update.getEmail());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Changes a user's password after verifying the current password, then revokes
     * all their refresh tokens so every device must log in again.
     *
     * @param id the ID of the user whose password will be changed
     * @param request the current password, new password, and confirmation
     * @throws ResourceNotFoundException if the user does not exist
     * @throws IncorrectCurrentPasswordException if the current password is incorrect
     * @throws PasswordMismatchException if the new password and confirmation differ
     * @throws PasswordReuseException if the new password matches the current password
     */
    @Override
    @Transactional
    public void changePasswordById(Long id, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IncorrectCurrentPasswordException();
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new PasswordReuseException();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // → Force re-login everywhere; existing access tokens expire on their own
        refreshTokenService.deleteAllByUser(user);
    }

    /**
     * Deletes a user and revokes their refresh tokens.
     *
     * <p>The last SUPER_ADMIN account cannot be deleted.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if the user does not exist
     * @throws LastSuperAdminException if the user is the last SUPER_ADMIN
     */
    @Override
    @Transactional
    public void deleteUserById(Long id) {
        UserEntity user = findWithRoles(id);

        if (isSuperAdmin(user) && isLastSuperAdmin()) {
            throw new LastSuperAdminException();
        }

        // → Access tokens are not stored server-side, so revoking refresh tokens is what stops the deleted user from renewing their session
        refreshTokenService.deleteAllByUser(user);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deleteAllUsers() {

        userRepository.deleteAll();
    }

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
        UserEntity user = findWithRoles(id);

        Set<Long> requestedIds = request.getRoleIds();
        List<RoleEntity> roles = roleRepository.findAllById(requestedIds);

        // → findAllById silently skips unknown ids, so compare sizes to detect them
        if (roles.size() != requestedIds.size()) {
            throw new ResourceNotFoundException("Role");
        }

        // → Prevent privilege escalation: callers can only grant permissions they hold.
        // → Role permissions are LAZY, so this must run inside the transaction.
        Set<String> permissionsToGrant = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());
        escalationGuard.assertCanGrant(permissionsToGrant);

        boolean losesSuperAdmin = isSuperAdmin(user)
                && roles.stream().noneMatch(role -> RoleNames.SUPER_ADMIN.equals(role.getName()));

        if (losesSuperAdmin && isLastSuperAdmin()) {
            throw new LastSuperAdminException();
        }

        user.setRoles(new HashSet<>(roles));

        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Returns the authenticated user's profile.
     *
     * @return the authenticated user's details and roles
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe() {
        return getUserById(CurrentUser.id());
    }

    /**
     * Updates the authenticated user's name and/or email.
     *
     * @param update the fields to update
     * @return the updated user's details
     * @see #updateUserById(Long, UpdateUserRequest)
     */
    @Override
    @Transactional
    public UserResponse updateMe(UpdateUserRequest update) {
        return updateUserById(CurrentUser.id(), update);
    }

    /**
     * Changes the authenticated user's password.
     *
     * @param request the current password, new password, and confirmation
     * @see #changePasswordById(Long, ChangePasswordRequest)
     */
    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        changePasswordById(CurrentUser.id(), request);
    }

    /**
     * Deletes the authenticated user's own account.
     *
     * <p>The supplied ID must match the authenticated user's ID.
     *
     * @param id the ID of the account to delete; must match the authenticated user's ID
     * @throws AccessDeniedException if the ID belongs to another user
     * @throws ResourceNotFoundException if the user does not exist
     * @throws LastSuperAdminException if the caller is the last SUPER_ADMIN
     */
    @Override
    @Transactional
    public void deleteMyAccount(Long id) {
        // → Compare against the token's id before touching the database
        if (!CurrentUser.id().equals(id)) {
            throw new AccessDeniedException("Cannot delete another user's account");
        }

        deleteUserById(id);
    }

    // HELPER

    /**
     * Finds a user by ID with their roles loaded.
     *
     * @param id the ID of the user to find
     * @return the user with their roles
     * @throws ResourceNotFoundException if the user does not exist
     */
    private UserEntity findWithRoles(Long id) {
        return userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));
    }

    /**
     * Checks whether a user has the SUPER_ADMIN role.
     *
     * @param user the user to check
     * @return {@code true} if the user is a SUPER_ADMIN; otherwise {@code false}
     */
    private boolean isSuperAdmin(UserEntity user) {
        return user.getRoles().stream()
                .anyMatch(role -> RoleNames.SUPER_ADMIN.equals(role.getName()));
    }

    /**
     * Checks whether only one SUPER_ADMIN account remains.
     *
     * @return {@code true} if one or no SUPER_ADMIN accounts remain;
     *         otherwise {@code false}
     */
    private boolean isLastSuperAdmin() {
        return userRepository.countByRolesName(RoleNames.SUPER_ADMIN) <= 1;
    }
}
