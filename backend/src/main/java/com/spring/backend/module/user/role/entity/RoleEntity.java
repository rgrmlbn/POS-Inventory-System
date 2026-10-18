package com.spring.backend.module.user.role.entity;

import com.spring.backend.module.shared.entity.BaseEntity;
import com.spring.backend.module.user.permission.entity.PermissionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private String description;

    // → Built-in roles (SUPER_ADMIN, USER) are protected from deletion/renaming
    @Column(nullable = false)
    private boolean systemRole = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();
}
