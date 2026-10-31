package com.spring.backend.module.user.user.controller;

import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for administrators managing any user account.
 *
 * <p>Each endpoint requires the permission noted on the method. Role assignment
 * is in {@link UserRoleController}, and endpoints for the authenticated user's
 * own account are in {@link UserProfileController}.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * Lists all users.
     *
     * <p>Requires {@code USER_READ}.
     *
     * @return 200 with a page of users
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(userAdminService.getAllUsers(name, page, size));
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
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable @Positive Long id) {

        return ResponseEntity.ok(userAdminService.getUserById(id));
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
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateUserRequest update) {

        return ResponseEntity.ok(userAdminService.updateUserById(id, update));
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
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable @Positive Long id) {

        userAdminService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }
}