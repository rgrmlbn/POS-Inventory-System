package com.spring.backend.module.user.role.service.interfaces;

import com.spring.backend.module.user.role.dto.request.CreateRoleRequest;
import com.spring.backend.module.user.role.dto.request.SetRolePermissionsRequest;
import com.spring.backend.module.user.role.dto.request.UpdateRoleRequest;
import com.spring.backend.module.user.role.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleById(Long id);
    RoleResponse createRole(CreateRoleRequest request);
    RoleResponse updateRole(Long id, UpdateRoleRequest request);
    RoleResponse setPermissions(Long id, SetRolePermissionsRequest request);
    void deleteRole(Long id);
}
