package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.role.LastSuperAdminException;
import com.spring.backend.module.security.util.CurrentUser;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserAdminService;
import com.spring.backend.module.user.user.service.interfaces.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements self-service operations for the authenticated user.
 *
 * <p>Every method uses the identity from the security context and delegates
 * the actual work to {@link UserAdminService}.
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserAdminService userAdminService;

    /**
     * Returns the authenticated user's profile.
     *
     * @return the authenticated user's details and roles
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe() {
        return userAdminService.getUserById(CurrentUser.id());
    }

    /**
     * Updates the authenticated user's name and/or email.
     *
     * @param update the fields to update
     * @return the updated user's details
     * @see UserAdminService#updateUserById(Long, UpdateUserRequest)
     */
    @Override
    @Transactional
    public UserResponse updateMe(UpdateUserRequest update) {
        return userAdminService.updateUserById(CurrentUser.id(), update);
    }

    /**
     * Changes the authenticated user's password.
     *
     * @param request the current password, new password, and confirmation
     * @see UserAdminService#changePasswordById(Long, ChangePasswordRequest)
     */
    @Override
    @Transactional
    public void changeMyPassword(ChangePasswordRequest request) {
        userAdminService.changePasswordById(CurrentUser.id(), request);
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

        userAdminService.deleteUserById(id);
    }
}