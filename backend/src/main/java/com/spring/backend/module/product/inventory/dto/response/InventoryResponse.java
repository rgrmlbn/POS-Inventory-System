package com.spring.backend.module.product.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String categoryName;
    private String sku;
    private Integer quantity;
    private Integer reorderLevel;
}
