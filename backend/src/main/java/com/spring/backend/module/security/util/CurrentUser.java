package com.spring.backend.module.security.util;

import com.spring.backend.module.security.principal.UserPrincipal;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for accessing information about the currently authenticated user.
 *
 * <p>Retrieves the current user's details from Spring Security's
 * {@link SecurityContextHolder}.
 */
@NoArgsConstructor
public final class CurrentUser {

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the ID of the current user
     * @throws AccessDeniedException if the user is not authenticated or the principal
     *                               is not an application {@link UserPrincipal}
     */
    public static Long id() {
        // -> Get the authentication details of the currently logged-in user.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // -> Verify that authentication exists and the principal represents an application user.
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            // -> Return the ID of the currently logged-in user.
            return principal.getUser().getId();
        }

        // -> Reject access when the current user cannot be identified as an authenticated application user.
        throw new AccessDeniedException("Not authenticated");
    }
}