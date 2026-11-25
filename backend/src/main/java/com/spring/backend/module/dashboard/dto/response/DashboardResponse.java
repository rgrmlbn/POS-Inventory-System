package com.spring.backend.module.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long totalProductCount;
    private Long totalProductQuantity;
    private BigDecimal totalInventoryValue;
    private BigDecimal totalPurchaseCost;
}
