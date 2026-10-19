package com.spring.backend.module.product.product.repository;

import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
