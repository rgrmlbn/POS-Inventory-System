package com.spring.backend.module.user.user.controller;

import com.spring.backend.module.user.user.dto.request.ChangePasswordRequest;
import com.spring.backend.module.user.user.dto.request.UpdateUserRequest;
import com.spring.backend.module.user.user.dto.response.UserResponse;
import com.spring.backend.module.user.user.service.interfaces.UserProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints acting on the authenticated user's own account.
 *
 * <p>These endpoints only require a valid login.
 */
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Returns the authenticated user's profile, including their roles.
     *
     * @return 200 with the user's profile
     */
    @GetMapping
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userProfileService.getMe());
    }

    /**
     * Updates the authenticated user's name and/or email.
     *
     * <p>Fields that are null or blank are left unchanged.
     *
     * @param update the fields to change
     * @return 200 with the updated profile, or 409 if the email is already taken
     */
    @PatchMapping
    public ResponseEntity<UserResponse> updateMe(
            @RequestBody @Valid UpdateUserRequest update) {
        return ResponseEntity.ok(userProfileService.updateMe(update));
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
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changeMyPassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        userProfileService.changeMyPassword(request);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyAccount(@PathVariable @Positive Long id) {
        userProfileService.deleteMyAccount(id);
        return ResponseEntity.noContent().build();
    }
}