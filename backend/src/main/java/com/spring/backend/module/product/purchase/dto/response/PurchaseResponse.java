package com.spring.backend.module.product.purchase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    private Long id;
    private String productName;
    private Integer purchaseQuantity;
    private Double unitCost;
    private Double totalCost;   // purchaseQuantity * unitCost, set by the service
    private String supplierName;
    private String note;
}
