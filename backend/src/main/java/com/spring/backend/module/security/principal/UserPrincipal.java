
package com.spring.backend.module.security.principal;

import com.spring.backend.module.user.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    // → Store the user entity containing the user's account details, roles, and permissions.
    private final UserEntity user;

    @Override
    public String getUsername() {
        // → Use the user's email address as their username for authentication.
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // → Get all permissions from the user's assigned roles and convert them into Spring Security authorities.
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        // → Return the user's hashed password for Spring Security authentication.
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        // → Indicate that the user's account has not expired.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // → Indicate that the user's account is not locked.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // → Indicate that the user's password or other credentials have not expired.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // → Indicate that the user's account is enabled.
        return true;
    }

    public UserEntity getUser() {
        // → Expose the underlying user entity for use in services or method-level security expressions.
        return user;
    }

    public Long getId() {
        // -> Expose the user's unique identifier for use in services or method-level security expressions.
        return user.getId();
    }
}
