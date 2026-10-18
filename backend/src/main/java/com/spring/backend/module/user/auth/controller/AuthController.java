package com.spring.backend.module.user.auth.controller;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RefreshTokenRequest;
import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints.
 *
 * <p>{@code register}, {@code login} and {@code refresh} are public. {@code logout}
 * requires a valid access token. Public paths are configured in {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new account with the default USER role.
     *
     * @param request the name, email and password
     * @return 201 with the created account, or 409 if the email is already registered
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Authenticates a user and issues an access token and a refresh token.
     *
     * @param request the email and password
     * @return 200 with both tokens, 401 if the credentials are wrong,
     *         or 429 if the email or IP has exceeded the login rate limit
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Exchanges a valid refresh token for a new access token and a new refresh token.
     * The submitted refresh token is consumed and cannot be reused.
     *
     * @param request the refresh token
     * @return 200 with the new tokens, or 401 if the token is invalid, expired or revoked
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Logs out the authenticated user on every device: all refresh tokens are deleted
     * and the current access token is blacklisted until it expires.
     *
     * @return 204 on success
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}