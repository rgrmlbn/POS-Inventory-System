package com.spring.backend.module.user.permission.repository;

import com.spring.backend.module.user.permission.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    List<PermissionEntity> findAllByNameIn(Collection<String> names);
}
