package com.spring.backend.module.product.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.Bidi;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long productCount;
    private Long inventoryProductQuantity;
    private Long outOfStockProductCount;
    private BigDecimal inventoryValue;
    private BigDecimal purchaseTotalCost;
    private BigDecimal saleTotalAmount;
}
