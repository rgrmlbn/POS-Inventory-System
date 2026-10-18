package com.spring.backend.module.user.user.repository;

import com.spring.backend.module.user.user.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link UserEntity}.
 *
 * <p>User roles and role permissions are lazily loaded. Methods named
 * {@code findWithRoles...} fetch them eagerly through an entity graph, so they are
 * safe to use outside a transaction (for example in the JWT filter).
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    /**
     * Finds a user by email without loading roles.
     * Use {@link #findWithRolesByEmail(String)} when authorities are needed.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds a user by email together with their roles and each role's permissions,
     * in a single query. Used to build the authenticated principal on every request.
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserEntity> findWithRolesByEmail(String email);

    /**
     * Finds a user by id together with their roles and role permissions,
     * in a single query.
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserEntity> findWithRolesById(Long id);

    /**
     * Returns all users with their roles and permissions, avoiding extra queries
     * when they are mapped to responses.
     */
    @Override
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    List<UserEntity> findAll();

    /**
     * Counts the users holding the role with the given name.
     * Used to ensure the last SUPER_ADMIN is never removed or demoted.
     */
    long countByRolesName(String name);

    /**
     * Checks whether any user is assigned the role with the given id.
     * Used to block deleting a role that is still in use.
     */
    boolean existsByRolesId(Long roleId);

    /**
     * Finds a user by id together with their roles and each role's permissions,
     * in a single query. Used to build the authenticated principal from the JWT.
     */
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<UserEntity> findWithAuthoritiesById(Long id);
}