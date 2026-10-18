package com.spring.backend.module.user.role.bootstrap;

import com.spring.backend.module.security.permission.Perm;
import com.spring.backend.module.security.permission.RoleNames;
import com.spring.backend.module.user.permission.entity.PermissionEntity;
import com.spring.backend.module.user.permission.repository.PermissionRepository;
import com.spring.backend.module.user.role.entity.RoleEntity;
import com.spring.backend.module.user.role.repository.RoleRepository;
import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Initializes the application's role-based access control (RBAC) data on startup.
 *
 * <p>Keeps the permissions table in sync with {@link Perm}: missing permissions are
 * created, and permissions no longer declared in code are removed from every role
 * and deleted. Creates the built-in SUPER_ADMIN and USER roles when missing, and
 * synchronizes the SUPER_ADMIN role with all available permissions on every startup.
 *
 * <p>Creates the initial SUPER_ADMIN account from application configuration
 * only when no SUPER_ADMIN exists and the configured email is not already in use.
 * Repeated executions are idempotent and do not recreate existing records.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RbacBootstrap implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    @Value("${app.bootstrap-admin.name:Super Admin}")
    private String adminName;

    /**
     * Initializes permissions, built-in roles, and the initial administrator.
     *
     * <p>Runs within a transaction so the bootstrap operations are committed
     * together or rolled back if an unchecked exception occurs.
     *
     * @param args the application arguments supplied at startup
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // → Make the permissions table match the permissions declared in Perm.
        Map<String, PermissionEntity> permissions = syncPermissions();

        // → Create or synchronize the built-in SUPER_ADMIN role.
        RoleEntity superAdmin = roleRepository.findByName(RoleNames.SUPER_ADMIN).orElseGet(() ->
                RoleEntity.builder()
                        .name(RoleNames.SUPER_ADMIN)
                        .description("Full access. Cannot be modified or deleted.")
                        .systemRole(true)
                        .build());

        // → Ensure SUPER_ADMIN always has every declared permission.
        superAdmin.setPermissions(new HashSet<>(permissions.values()));
        superAdmin = roleRepository.save(superAdmin);

        // → Create the default USER role only when it does not already exist.
        roleRepository.findByName(RoleNames.USER).orElseGet(() ->
                roleRepository.save(RoleEntity.builder()
                        .name(RoleNames.USER)
                        .description("Default role for registered users.")
                        .systemRole(true)
                        .build()));

        // → Create the initial SUPER_ADMIN only with configured credentials,
        //   no existing SUPER_ADMIN, and an unused email.
        if (!adminEmail.isBlank() && !adminPassword.isBlank()
                && userRepository.countByRolesName(RoleNames.SUPER_ADMIN) == 0
                && !userRepository.existsByEmail(adminEmail)) {

            UserEntity admin = UserEntity.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .build();

            // → Assign the built-in SUPER_ADMIN role to the initial administrator.
            admin.getRoles().add(superAdmin);
            userRepository.save(admin);

            log.info("Bootstrap super admin created: {}", adminEmail);
        }
    }

    /**
     * Makes the permissions table match {@link Perm}.
     *
     * @return the declared permissions keyed by name; stale permissions are not included
     */
    private Map<String, PermissionEntity> syncPermissions() {
        Set<String> declared = new HashSet<>(Perm.all());
        Map<String, PermissionEntity> current = new HashMap<>();
        List<PermissionEntity> stale = new ArrayList<>();

        // → Split the existing permissions into declared ones and stale ones.
        for (PermissionEntity permission : permissionRepository.findAll()) {
            if (declared.contains(permission.getName())) {
                current.put(permission.getName(), permission);
            } else {
                stale.add(permission);
            }
        }

        // → Create each declared permission that does not exist in the database yet.
        for (String name : declared) {
            current.computeIfAbsent(name, n ->
                    permissionRepository.save(PermissionEntity.builder().name(n).build()));
        }

        // → Remove permissions that are no longer declared in Perm.
        if (!stale.isEmpty()) {
            removeStalePermissions(stale);
        }

        return current;
    }

    /**
     * Detaches the given permissions from every role, then deletes them.
     *
     * <p>The detach step must come first: the role_permissions join table references
     * the permissions table, so deleting a permission that is still assigned fails.
     *
     * @param stale the permissions that exist in the database but not in {@link Perm}
     */
    private void removeStalePermissions(List<PermissionEntity> stale) {
        Set<Long> staleIds = stale.stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        // → Detach the stale permissions from every role that still holds them.
        List<RoleEntity> roles = roleRepository.findAll();
        for (RoleEntity role : roles) {
            role.getPermissions().removeIf(permission -> staleIds.contains(permission.getId()));
        }
        roleRepository.saveAll(roles);
        roleRepository.flush();

        // → Delete the stale permissions now that no role references them.
        permissionRepository.deleteAll(stale);

        log.warn("Removed permissions no longer declared in Perm: {}",
                stale.stream().map(PermissionEntity::getName).toList());
    }
}