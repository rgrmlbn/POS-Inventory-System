package com.spring.backend.module.product.dashboard.service.impl;

import com.spring.backend.module.product.dashboard.dto.response.DashboardResponse;
import com.spring.backend.module.product.dashboard.mapper.DashboardMapper;
import com.spring.backend.module.product.dashboard.service.interfaces.DashboardService;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import com.spring.backend.module.product.purchase.service.interfaces.PurchaseService;
import com.spring.backend.module.product.sale.service.interfaces.SaleService;
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
    private final SaleService saleService;
    private final DashboardMapper dashboardMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        Long productCount = productService.getProductCount();
        Long inventoryProductQuantity = inventoryService.getInventoryProductQuantity();
        Long outOfStockProductCount = inventoryService.getOutOfStockProductCount();
        BigDecimal inventoryValue = inventoryService.getInventoryValue();
        BigDecimal purchaseTotalCost = purchaseService.getPurchaseTotalCost();
        BigDecimal saleTotalAmount = saleService.getSaleTotalAmount();

        return dashboardMapper.toDashboardResponse(
                productCount,
                inventoryProductQuantity,
                outOfStockProductCount,
                inventoryValue,
                purchaseTotalCost,
                saleTotalAmount);
    }
}
