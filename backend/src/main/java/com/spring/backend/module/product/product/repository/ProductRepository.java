package com.spring.backend.module.product.product.repository;

import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllByProductNameContainingIgnoreCase(String productName, Pageable pageable);
}
