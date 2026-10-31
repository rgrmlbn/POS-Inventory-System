package com.spring.backend.module.product.inventory.repository;

import com.spring.backend.module.product.inventory.entity.StockMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

    @EntityGraph(attributePaths = {"product"})
    Page<StockMovementEntity> findAllByProductId(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product"})
    Page<StockMovementEntity> findAll(Pageable pageable);

}
