package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.security.principal.UserPrincipal;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.module.user.user.service.interfaces.UserIdDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads users for Spring Security.
 *
 * <p>{@link #loadUserByUsername(String)} is used at login, where the credentials
 * contain an email. {@link #loadUserById(Long)} is used on every authenticated
 * request, where the JWT subject is the user id.
 *
 * <p>Authorities come from the database each time rather than from the token, so
 * role and permission changes take effect immediately.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserIdDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by email, with their roles and permissions.
     *
     * @throws UsernameNotFoundException if no user has this email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Roles and permissions must be fetched here: callers run outside a
        // transaction, so lazy loading would fail later in getAuthorities()
        UserEntity user = userRepository.findWithRolesByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password."));

        return new UserPrincipal(user);
    }

    @Override
    public UserPrincipal loadUserById(Long id) throws UsernameNotFoundException {
        UserEntity user = userRepository.findWithAuthoritiesById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));

        return new UserPrincipal(user);
    }
}