package com.spring.backend.module.product.purchase.repository;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.purchase.entity.PurchaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<PurchaseEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.category"})
    Page<PurchaseEntity> findByProductProductNameContainingIgnoreCase(
            String productName,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<PurchaseEntity> findPurchaseById(Long id);

    @Query("SELECT SUM(purchaseEntity.totalCost) FROM  PurchaseEntity purchaseEntity")
    Optional<BigDecimal> sumPurchaseCost();
}
