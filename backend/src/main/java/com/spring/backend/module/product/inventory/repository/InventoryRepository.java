package com.spring.backend.module.product.inventory.repository;

import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<InventoryEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<InventoryEntity> findByProductProductNameContainingIgnoreCase(String productName, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<InventoryEntity> findInventoryById(Long id);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<InventoryEntity> findByProductId(Long productId);

    @Query("SELECT SUM(inventoryEntity.productQuantity) FROM InventoryEntity inventoryEntity")
    Optional<Long> sumProductQuantity();

    long countByProductQuantity(Integer productQuantity);

    @Query("SELECT SUM(inventoryEntity.productQuantity * productEntity.costPrice) " +
            "FROM InventoryEntity inventoryEntity " +
            "JOIN inventoryEntity.product productEntity")
    Optional<BigDecimal> sumInventoryValue();



}
