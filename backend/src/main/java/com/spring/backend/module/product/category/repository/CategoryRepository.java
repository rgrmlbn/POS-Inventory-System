package com.spring.backend.module.product.category.repository;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByCategoryName(String categoryName);

    Page<CategoryEntity> findAllByCategoryNameContainingIgnoreCase(
            String categoryName,
            Pageable pageable
    );
}
