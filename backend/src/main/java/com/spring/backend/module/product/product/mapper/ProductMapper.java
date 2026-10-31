package com.spring.backend.module.product.product.mapper;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductEntity toEntity(ProductCreateRequest request, CategoryEntity category) {

        return ProductEntity.builder()
                .productName(request.getProductName())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .category(category)
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .reorderLevel(request.getReorderLevel())
                .build();
    }

    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .productName(entity.getProductName())
                .sku(entity.getSku())
                .barcode(entity.getBarcode())
                .categoryName(entity.getCategory().getCategoryName())
                .costPrice(entity.getCostPrice())
                .sellingPrice(entity.getSellingPrice())
                .reorderLevel(entity.getReorderLevel())
                .build();
    }

}
