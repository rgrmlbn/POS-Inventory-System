package com.spring.backend.module.dashboard.mapper;

import com.spring.backend.module.dashboard.dto.response.DashboardResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DashboardMapper {
    public DashboardResponse toDashboardResponse(
            Long productCount,
            Long prroductQuantity,
            BigDecimal inventoryValue,
            BigDecimal purchaseCost) {
        return DashboardResponse.builder()
                .totalProductCount(productCount)
                .totalProductQuantity(prroductQuantity)
                .totalInventoryValue(inventoryValue)
                .totalPurchaseCost(purchaseCost)
                .build();
    }
}
