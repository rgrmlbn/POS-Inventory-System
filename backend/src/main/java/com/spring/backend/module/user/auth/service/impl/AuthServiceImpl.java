package com.spring.backend.module.user.auth.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.user.user.DuplicateEmailException;
import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.security.principal.UserPrincipal;
import com.spring.backend.module.security.util.CurrentUser;
import com.spring.backend.module.security.util.JwtUtil;
import com.spring.backend.module.user.auth.dto.request.LoginRequest;
import com.spring.backend.module.user.auth.dto.request.RefreshTokenRequest;
import com.spring.backend.module.user.auth.dto.request.RegisterRequest;
import com.spring.backend.module.user.auth.dto.response.AuthResponse;
import com.spring.backend.module.user.auth.dto.response.RegisterResponse;
import com.spring.backend.module.user.auth.service.interfaces.AuthService;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.role.repository.RoleRepository;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.mapper.UserMapper;
import com.spring.backend.module.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the authentication service.
 *
 * <p>Handles user registration, login, refresh-token rotation, and logout.
 * Integrates with Spring Security, JWT utilities, refresh-token management,
 * login rate limiting, and token blacklisting.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Registers a new user and assigns the default USER role.
     *
     * @param request the registration details
     * @return the response containing the newly registered user's information
     * @throws DuplicateEmailException if the email is already registered
     * @throws IllegalStateException if the default USER role does not exist
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        // → The USER role is created by RbacBootstrap at startup. If it is missing, the application is misconfigured, which is a server error rather than a client one.
        RoleEntity defaultRole = roleRepository.findByName(RoleNames.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role"));

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(defaultRole);

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toRegisterResponse(savedUser);
    }

    /**
     * Authenticates a user and issues access and refresh tokens.
     *
     * @param request the login credentials
     * @return the response containing the access token and refresh token
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(principal))
                .refreshToken(refreshTokenService.createRefreshToken(principal.getUser()))
                .build();
    }

    /**
     * Validates and rotates a refresh token, issuing a new access token.
     *
     * <p>This method is deliberately not transactional because
     * {@code validateRefreshToken} may delete an expired token before throwing
     * an exception. A surrounding transaction could roll back that deletion.
     *
     * @param request the refresh-token request
     * @return the response containing the new access token and refresh token
     */
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        // → Only the user's email is needed to issue the access token, so roles do not need to be loaded here.
        UserPrincipal principal = new UserPrincipal(oldToken.getUser());

        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);
        String newAccessToken = jwtUtil.generateAccessToken(principal);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * Logs out the authenticated user by revoking refresh tokens
     * and blacklisting the current access token.
     *
     * @throws AccessDeniedException if no valid authenticated access token is available
     */
    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // → JwtFilter stores the raw access token as the authentication credentials.
        if (authentication == null || !(authentication.getCredentials() instanceof String accessToken)) {
            throw new AccessDeniedException("Not authenticated");
        }

        // → A reference is sufficient because deleting tokens only requires the user's ID.
        UserEntity user = userRepository.getReferenceById(CurrentUser.id());

        refreshTokenService.deleteAllByUser(user);
        tokenBlacklistService.blacklist(accessToken);

        SecurityContextHolder.clearContext();
    }
}