package com.spring.backend.module.user.user.service.interfaces;

import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

/**
 * Operations an administrator performs on any user account.
 */
public interface UserAdminService {

    Page<UserResponse> getAllUsers(String name, int page, int size);

    UserResponse getUserById(Long id);

    UserResponse updateUserById(Long id, UpdateUserRequest update);

    void changePasswordById(Long id, ChangePasswordRequest request);

    void deleteUserById(Long id);

}