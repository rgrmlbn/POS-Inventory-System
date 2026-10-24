package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.LastSuperAdminException;
import com.spring.backend.module.exception.user.user.DuplicateEmailException;
import com.spring.backend.module.exception.user.user.IncorrectCurrentPasswordException;
import com.spring.backend.module.exception.user.user.PasswordMismatchException;
import com.spring.backend.module.exception.user.user.PasswordReuseException;
import com.spring.backend.module.security.util.SuperAdminGuard;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.interfaces.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements administrative operations on user accounts.
 *
 * <p>Methods ending in {@code ById} operate on a specified user and are expected
 * to be protected by permission checks at the controller.
 *
 * <p>Account deletion and password changes revoke refresh tokens to prevent
 * affected users from renewing their sessions.
 */
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final SuperAdminGuard superAdminGuard;

    /**
     * Returns a page of users with their roles, optionally filtered by name.
     *
     * @param name part of the user's name to search for
     * @param page the zero-based page number
     * @param size the page size
     * @return a page of users with their roles
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String name, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<UserEntity> users = userRepository.findAllByNameContainingIgnoreCase(name, pageable);

        return users.map(userMapper::toResponse);
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

        UserEntity user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        return userMapper.toResponse(user);
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
        UserEntity user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

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
        UserEntity user = userRepository.findWithRolesById(id)
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
        UserEntity user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        if (superAdminGuard.isSuperAdmin(user) && superAdminGuard.isLastSuperAdmin()) {
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
}