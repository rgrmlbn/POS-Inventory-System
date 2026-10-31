package com.spring.backend.module.product.inventory.mapper;

import com.spring.backend.module.product.inventory.dto.request.InventoryCreateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryEntity toEntity(InventoryCreateRequest request, ProductEntity product){

        return InventoryEntity.builder()
                .product(product)
                .productQuantity(request.getQuantity())
                .build();

    }

    public InventoryResponse toResponse(InventoryEntity entity){

        return InventoryResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getProductName())
                .categoryName(entity.getProduct().getCategory().getCategoryName())
                .sku(entity.getProduct().getSku())
                .quantity(entity.getProductQuantity())
                .reorderLevel(entity.getProduct().getReorderLevel())
                .build();
    }

}
