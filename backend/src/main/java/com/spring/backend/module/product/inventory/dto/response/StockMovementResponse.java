package com.spring.backend.module.product.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StockMovementResponse {

    private Long id;

    private Long productId;
    private String productName;
    private String sku;

    private String movementType;

    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;

    private String reason;

    private LocalDateTime createdAt;
}
