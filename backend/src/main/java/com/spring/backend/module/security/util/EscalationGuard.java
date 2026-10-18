package com.spring.backend.module.security.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/** Prevents privilege escalation: you can only hand out permissions you hold yourself. */
@Component
public class EscalationGuard {

    /**
     * Rejects granting any permission the authenticated caller does not hold.
     *
     * @param permissionNames permissions being granted
     * @throws AccessDeniedException if there is no authenticated caller or the
     *         caller lacks any requested permission
     */
    public void assertCanGrant(Collection<String> permissionNames) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        Set<String> held = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        if (!held.containsAll(permissionNames)) {
            throw new AccessDeniedException("Cannot grant permissions you do not hold");
        }
    }
}
