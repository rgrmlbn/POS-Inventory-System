package com.spring.backend.module.product.product.repository;

import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllByProductNameContainingIgnoreCase(
            String productName,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllBySkuContainingIgnoreCase(String sku, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Optional<ProductEntity> findProductById(Long productId);

    boolean existsBySku(String sku);
}
