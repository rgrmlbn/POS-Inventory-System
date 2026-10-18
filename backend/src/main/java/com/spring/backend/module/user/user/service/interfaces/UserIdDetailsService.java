package com.spring.backend.module.user.user.service.interfaces;

import com.spring.backend.module.security.principal.UserPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads a principal by user id, for authenticating requests from the JWT subject.
 */
public interface UserIdDetailsService {

    /**
     * Loads a user by id, with their roles and permissions.
     *
     * @throws UsernameNotFoundException if no user has this id (for example, the account was deleted)
     */
    UserPrincipal loadUserById(Long id) throws UsernameNotFoundException;
}
