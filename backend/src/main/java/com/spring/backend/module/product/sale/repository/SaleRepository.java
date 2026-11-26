package com.spring.backend.module.product.sale.repository;

import com.spring.backend.module.product.sale.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {


    Page<SaleEntity> findAll(Pageable pageable);

    Page<SaleEntity> findDistinctBySaleItemsProductProductNameContainingIgnoreCase(String productName, Pageable pageable);

    @EntityGraph(attributePaths = {"saleItems", "saleItems.product"})
    Optional<SaleEntity> findSaleById(Long id);

    @Query("SELECT SUM(saleEntity.totalAmount) FROM  SaleEntity saleEntity")
    Optional<BigDecimal> sumSaleTotalAmount();


}
