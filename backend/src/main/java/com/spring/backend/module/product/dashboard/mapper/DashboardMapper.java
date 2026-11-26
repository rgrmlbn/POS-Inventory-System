package com.spring.backend.module.product.dashboard.mapper;

import com.spring.backend.module.product.dashboard.dto.response.DashboardResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DashboardMapper {
    public DashboardResponse toDashboardResponse(
            Long productCount,
            Long inventoryProductQuantity,
            Long outOfStockProductCount,
            BigDecimal inventoryValue,
            BigDecimal purchaseTotalCost,
            BigDecimal saleTotalAmount) {
        return DashboardResponse.builder()
                .productCount(productCount)
                .inventoryProductQuantity(inventoryProductQuantity)
                .outOfStockProductCount(outOfStockProductCount)
                .inventoryValue(inventoryValue)
                .purchaseTotalCost(purchaseTotalCost)
                .saleTotalAmount(saleTotalAmount)
                .build();
    }
}
