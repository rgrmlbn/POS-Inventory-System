package com.spring.backend.module.user.role.repository;

import com.spring.backend.module.user.role.entity.RoleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link RoleEntity}.
 *
 * <p>Role permissions are lazily loaded. Methods annotated with {@link EntityGraph}
 * fetch permissions eagerly to ensure they are available when mapping roles to
 * responses, avoiding additional queries when accessing permissions.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByName(String name);

    /**
     * Finds a role by its name without explicitly loading its permissions.
     *
     * @param name the name of the role to find
     * @return an {@link Optional} containing the role if found
     */
    Optional<RoleEntity> findByName(String name);

    /**
     * Finds a role by ID together with its permissions, in a single query.
     *
     * @param id the ID of the role to find
     * @return an {@link Optional} containing the role and its permissions if found
     */
    @EntityGraph(attributePaths = "permissions")
    Optional<RoleEntity> findWithPermissionsById(Long id);

    /**
     * Returns all roles together with their permissions, avoiding additional
     * queries when permissions are accessed while mapping roles to responses.
     *
     * @return all roles with their permissions
     */
    @Override
    @EntityGraph(attributePaths = "permissions")
    List<RoleEntity> findAll();

    /**
     * Returns the roles matching the given IDs together with their permissions,
     * avoiding additional queries when accessing permissions.
     *
     * @param ids the IDs of the roles to retrieve
     * @return the matching roles with their permissions
     */
    @Override
    @EntityGraph(attributePaths = "permissions")
    List<RoleEntity> findAllById(Iterable<Long> ids);
}