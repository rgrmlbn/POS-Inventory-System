package com.spring.backend.module.user.token.repository;

import com.spring.backend.module.user.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for managing refresh token records.
 *
 * <p>Provides database operations for retrieving, deleting, and revoking
 * refresh tokens associated with user accounts.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its token value.
     *
     * @param token the refresh token value to search for
     * @return an {@link Optional} containing the matching refresh token, if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens associated with a user.
     *
     * @param userId the ID of the user whose refresh tokens should be deleted
     */
    void deleteAllByUserId(Long userId);

    /**
     * Marks all refresh tokens belonging to a user as revoked.
     *
     * <p>Uses a bulk update query to revoke the user's tokens without
     * loading each token entity individually.
     *
     * @param userId the ID of the user whose refresh tokens should be revoked
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);
}