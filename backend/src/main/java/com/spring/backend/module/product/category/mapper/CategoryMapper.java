package com.spring.backend.module.product.category.mapper;

import com.spring.backend.module.product.category.dto.request.CategoryCreateRequest;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryEntity toEntity(CategoryCreateRequest request) {
        return CategoryEntity.builder()
                .categoryName(request.getCategoryName())
                .build();
    }

    public CategoryResponse toResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .build();
    }
}
