package com.spring.backend.module.user.token.service.impl;

import com.spring.backend.module.config.JwtProperties;
import com.spring.backend.module.exception.user.auth.InvalidTokenException;
import com.spring.backend.module.exception.user.auth.TokenExpiredException;
import com.spring.backend.module.exception.user.auth.TokenRevokedException;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.repository.RefreshTokenRepository;
import com.spring.backend.module.user.token.service.interfaces.RefreshTokenService;
import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Implementation of the refresh token service.
 *
 * <p>Handles refresh token creation, validation, rotation, revocation, and deletion.
 * Stores hashed refresh tokens in the database instead of their original values
 * to reduce the risk of exposing usable tokens if the database is compromised.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Hashes a raw refresh token using the SHA-256 algorithm.
     *
     * @param rawToken the original refresh token value
     * @return the Base64-encoded SHA-256 hash
     * @throws RuntimeException if the SHA-256 algorithm is unavailable
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash refresh token", e);
        }
    }

    /**
     * Creates and persists a refresh token for the specified user.
     *
     * <p>The raw token is returned to the caller, while only its hash is stored
     * in the database. The token's expiration is determined by the configured
     * refresh token lifetime.
     *
     * @param user the user who owns the refresh token
     * @return the raw refresh token value
     */
    @Override
    public String createRefreshToken(UserEntity user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(hashedToken)
                .user(user)
                .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    /**
     * Validates a raw refresh token and retrieves its database record.
     *
     * <p>Checks whether the token exists, has been revoked, or has expired.
     * Expired tokens are deleted before the expiration exception is thrown.
     *
     * @param rawToken the raw refresh token provided by the client
     * @return the validated refresh token entity
     * @throws InvalidTokenException if the token does not exist
     * @throws TokenRevokedException if the token has been revoked
     * @throws TokenExpiredException if the token has expired
     */
    @Override
    public RefreshToken validateRefreshToken(String rawToken) {
        // → Hash the supplied token to match the value stored in the database.
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(InvalidTokenException::new);

        // → Reject tokens that have already been revoked.
        if (refreshToken.isRevoked()) {
            throw new TokenRevokedException();
        }

        // → Remove expired tokens before reporting that they are no longer valid.
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException();
        }

        return refreshToken;
    }

    /**
     * Rotates a refresh token by replacing the existing token with a new one.
     *
     * <p>Deletes the old token record and creates a replacement for the same user.
     * The caller should validate the old token before invoking this method.
     *
     * @param oldToken the existing refresh token entity
     * @return the raw value of the newly created refresh token
     */
    @Override
    public String rotateRefreshToken(RefreshToken oldToken) {
        UserEntity user = oldToken.getUser();

        // → Remove the old token so it cannot be reused after rotation.
        refreshTokenRepository.delete(oldToken);

        return createRefreshToken(user);
    }

    /**
     * Revokes all refresh tokens belonging to the specified user.
     *
     * @param user the user whose refresh tokens should be revoked
     */
    @Override
    public void revokeAllByUser(UserEntity user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    /**
     * Deletes all refresh tokens belonging to the specified user.
     *
     * @param user the user whose refresh tokens should be deleted
     */
    @Override
    public void deleteAllByUser(UserEntity user) {
        refreshTokenRepository.deleteAllByUserId(user.getId());
    }
}