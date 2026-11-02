package com.spring.backend.module.product.purchase.mapper;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.purchase.dto.request.PurchaseCreateRequest;
import com.spring.backend.module.product.purchase.dto.response.PurchaseResponse;
import com.spring.backend.module.product.purchase.entity.PurchaseEntity;
import org.springframework.stereotype.Component;

@Component
public class PurchaseMapper {

    public PurchaseEntity toEntity(PurchaseCreateRequest request, ProductEntity product) {
        return PurchaseEntity.builder()
                .product(product)
                .purchaseQuantity(request.getPurchaseQuantity())
                .unitCost(request.getUnitCost())
                .supplierName(request.getSupplierName())
                .note(request.getNote())
                .build();
    }

    public PurchaseResponse toResponse(PurchaseEntity entity){
        return PurchaseResponse.builder()
                .id(entity.getId())
                .productName(entity.getProduct().getProductName())
                .purchaseQuantity(entity.getPurchaseQuantity())
                .unitCost(entity.getUnitCost())
                .totalCost(entity.getTotalCost())
                .supplierName(entity.getSupplierName())
                .note(entity.getNote())
                .build();
    }
}
