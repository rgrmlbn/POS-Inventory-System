package com.spring.backend.module.user.user.service.interfaces;

import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;

/**
 * Self-service operations for the authenticated user's own account.
 */
public interface UserProfileService {

    UserResponse getMe();

    UserResponse updateMe(UpdateUserRequest update);

    void changeMyPassword(ChangePasswordRequest request);

    void deleteMyAccount(Long id);
}