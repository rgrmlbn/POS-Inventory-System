package com.spring.backend.module.security.util;

import com.spring.backend.module.config.JwtProperties;
import com.spring.backend.module.security.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates, parses and validates signed JSON Web Tokens (JWT).
 *
 * <p>Tokens are signed with the secret from {@link JwtProperties}. The token subject is
 * the user's id, not their email, so a token stays valid when the user changes their email.
 * Roles and permissions are deliberately not stored in the token; they are loaded from the
 * database on each request so that changes apply immediately.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final JwtProperties jwtProperties;

    /**
     * Builds the HMAC key used to sign and verify tokens from the configured secret.
     *
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates a signed access token whose subject is the user's id.
     *
     * @param principal the authenticated user
     * @return the compact, signed JWT
     */
    public String generateAccessToken(UserPrincipal principal) {
        return generateToken(new HashMap<>(), String.valueOf(principal.getId()),
                jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Builds and signs a JWT.
     *
     * @param extraClaims additional claims to include
     * @param subject     the token subject
     * @param expiration  how long the token is valid, in milliseconds
     * @return the compact, signed JWT
     */
    private String generateToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Verifies the token's signature and returns its claims.
     *
     * @param token the JWT to parse
     * @return the token's claims
     * @throws JwtException if the token is malformed, tampered with or expired
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts a single claim using the given resolver.
     *
     * @param token          the JWT to read
     * @param claimsResolver selects the claim from the token's claims
     * @param <T>            the claim's type
     * @return the resolved claim
     * @throws JwtException if the token is invalid or expired
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Returns the user id stored in the token's subject.
     *
     * @param token the JWT to read
     * @return the user id
     * @throws MalformedJwtException if the subject is not a number, as in tokens issued
     *                               before the subject changed from email to user id
     * @throws JwtException          if the token is otherwise invalid or expired
     */
    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new MalformedJwtException("Token subject is not a user id");
        }
    }

    /**
     * Returns the token's expiration time.
     *
     * @param token the JWT to read
     * @return the expiration time
     */
    public Instant extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).toInstant();
    }
    
}