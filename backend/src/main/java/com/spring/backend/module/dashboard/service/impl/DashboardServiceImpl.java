package com.spring.backend.module.dashboard.service.impl;

import com.spring.backend.module.dashboard.dto.response.DashboardResponse;
import com.spring.backend.module.dashboard.mapper.DashboardMapper;
import com.spring.backend.module.dashboard.service.interfaces.DashboardService;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import com.spring.backend.module.product.purchase.repository.PurchaseRepository;
import com.spring.backend.module.product.purchase.service.interfaces.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final PurchaseService purchaseService;
    private final DashboardMapper dashboardMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        Long productCount = productService.getTotalProductCount();
        Long productQuantity = inventoryService.getTotalProductQuantity();
        BigDecimal inventoryValue = inventoryService.getTotalInventoryValue();
        BigDecimal purchaseCost = purchaseService.getTotalPurchaseCost();

        return dashboardMapper.toDashboardResponse(
                productCount,
                productQuantity,
                inventoryValue,
                purchaseCost);
    }
}
