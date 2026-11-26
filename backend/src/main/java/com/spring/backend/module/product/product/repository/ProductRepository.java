package com.spring.backend.module.product.product.repository;

import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = {"category", "inventory"})
    Page<ProductEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllByProductNameContainingIgnoreCase(
            String productName,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<ProductEntity> findAllBySkuContainingIgnoreCase(String sku, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "inventory"})
    Optional<ProductEntity> findProductById(Long id);

    @EntityGraph(attributePaths = {"category", "inventory"})
    @Query("SELECT productEntity FROM ProductEntity productEntity " +
            "JOIN productEntity.inventory inventoryEntity " +
            "WHERE inventoryEntity.productQuantity = 0")
    List<ProductEntity> findAllOutOfStock();

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);
}
