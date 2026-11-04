package com.spring.backend.module.product.sale.repository;

import com.spring.backend.module.product.sale.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {


    Page<SaleEntity> findAll(Pageable pageable);

    Page<SaleEntity> findDistinctBySaleItemsProductProductNameContainingIgnoreCase(String productName, Pageable pageable);

    @EntityGraph(attributePaths = {"saleItems", "saleItems.product"})
    Optional<SaleEntity> findWithItemsById(Long id);

}
