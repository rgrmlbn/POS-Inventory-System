package com.spring.backend.module.user.user.service.interfaces;

import com.spring.backend.module.user.user.dto.request.AssignRolesRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;

/**
 * Role assignment for user accounts.
 */
public interface UserRoleService {

    UserResponse assignRolesById(Long id, AssignRolesRequest request);
}