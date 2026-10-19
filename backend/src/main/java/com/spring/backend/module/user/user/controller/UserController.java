package com.spring.backend.module.user.user.controller;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.user.user.dto.request.AssignRolesRequest;
import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;

import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for user management.
 *
 * <p>Endpoints under {@code /me} act on the authenticated user's own account and
 * only require a valid login. All other endpoints act on any user and require
 * the permission noted on each method.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * Lists all users.
     *
     * <p>Requires {@code USER_READ}.
     *
     * @return 200 with every user
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + Perm.USER_READ + "')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    /**
     * Returns a single user by id.
     *
     * <p>Requires {@code USER_READ}.
     *
     * @param id the user's id
     * @return 200 with the user, or 404 if not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.USER_READ + "')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Updates a user's name and/or email.
     *
     * <p>Requires {@code USER_UPDATE}.
     *
     * @param id the user's id
     * @param update the fields to change
     * @return 200 with the updated user, 404 if not found, or 409 if the email is already taken
     */
    @PatchMapping("/{id}/update")
    @PreAuthorize("hasAuthority('" + Perm.USER_UPDATE + "')")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateUserRequest update) {
        return ResponseEntity.ok(userService.updateUserById(id, update));
    }

    /**
     * Deletes a user and revokes their refresh tokens.
     *
     * <p>Requires {@code USER_DELETE}.
     *
     * @param id the user's id
     * @return 204 on success, 404 if not found, or 409 if the user is the last SUPER_ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.USER_DELETE + "')")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('" + Perm.USER_DELETE_ALL + "')")
    public ResponseEntity<Void> deleteAllUsers() {

        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

    /**
     * Replaces the roles assigned to a user.
     *
     * <p>Requires {@code USER_ASSIGN_ROLE}. The caller can only grant roles whose
     * permissions they already hold.
     *
     * @param id the user's id
     * @param request the ids of the roles the user should end up with
     * @return 200 with the updated user, 403 if the grant would escalate privileges,
     *         404 if the user or a role is not found, or 409 if it would remove the last SUPER_ADMIN
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('" + Perm.USER_ASSIGN_ROLE + "')")
    public ResponseEntity<UserResponse> assignRoles(
            @PathVariable @Positive Long id,
            @RequestBody @Valid AssignRolesRequest request) {
        return ResponseEntity.ok(userService.assignRolesById(id, request));
    }

    /**
     * Returns the authenticated user's profile, including their roles.
     *
     * @return 200 with the user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    /**
     * Updates the authenticated user's name and/or email.
     *
     * <p>Fields that are null or blank are left unchanged.
     *
     * @param update the fields to change
     * @return 200 with the updated profile, or 409 if the email is already taken
     */
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @RequestBody @Valid UpdateUserRequest update) {
        return ResponseEntity.ok(userService.updateMe(update));
    }

    /**
     * Changes the authenticated user's password.
     *
     * <p>All refresh tokens are revoked, so the user must log in again on every device.
     *
     * @param request the current password, the new password and its confirmation
     * @return 204 on success, 401 if the current password is wrong,
     *         400 if the confirmation does not match or the new password is reused
     */
    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changeMyPassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the authenticated user's own account.
     *
     * <p>The id in the path must match the authenticated user's id.
     *
     * @param id the id of the account to delete (must be the caller's own)
     * @return 204 on success, 403 if the id belongs to another user,
     *         409 if the caller is the last SUPER_ADMIN
     */
    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> deleteMyAccount(@PathVariable @Positive Long id) {
        userService.deleteMyAccount(id);
        return ResponseEntity.noContent().build();
    }
}
