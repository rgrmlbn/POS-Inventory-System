package com.spring.backend.module.user.permission.entity;

import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

}
