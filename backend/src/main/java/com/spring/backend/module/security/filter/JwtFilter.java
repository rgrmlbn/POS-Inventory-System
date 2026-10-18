package com.spring.backend.module.security.filter;

import com.spring.backend.module.security.principal.UserPrincipal;
import com.spring.backend.module.security.util.JwtUtil;
import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.module.user.user.service.interfaces.UserIdDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates requests that carry a valid bearer token.
 *
 * <p>The token's subject is the user id. The user is loaded from the database on every
 * request, so roles and permissions are always current and are not stored in the token.
 *
 * <p>This filter never rejects a request itself. When the token is missing, invalid,
 * expired, blacklisted or refers to a deleted user, the request simply continues
 * unauthenticated, and the security configuration responds with 401 where authentication
 * is required.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserIdDetailsService userIdDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Establishes authentication for the request when its bearer token is valid.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if the rest of the chain fails
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(BEARER_PREFIX.length());

        // -> Logout blacklists the token, so it must be rejected even though it has not expired
        if (tokenBlacklistService.isBlacklisted(jwt)) {
            log.warn("Rejected blacklisted JWT for URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        Long userId;
        try {
            userId = jwtUtil.extractUserId(jwt);
        } catch (ExpiredJwtException e) {
            log.warn("JWT is expired: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (JwtException | IllegalArgumentException e) {
            // -> Covers bad signatures, malformed tokens, and tokens issued before the
            // -> subject changed from email to user id
            log.warn("Invalid JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserPrincipal principal = userIdDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(principal, jwt, principal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (UsernameNotFoundException e) {
                // -> The account was deleted after this token was issued. Without this catch the
                // -> exception escapes the filter and the client gets a 500 instead of a 401
                log.warn("JWT references a user that no longer exists: {}", userId);
            }
        }

        filterChain.doFilter(request, response);
    }
}