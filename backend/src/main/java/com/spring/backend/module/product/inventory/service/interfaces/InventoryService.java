package com.spring.backend.module.product.inventory.service.interfaces;

import com.spring.backend.module.product.inventory.dto.request.StockAdjustmentRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.dto.response.StockMovementResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

public interface InventoryService {

    Page<InventoryResponse> getAllInventory(String productName, int page, int size);

    void initializeInventory(ProductEntity product);

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request);

    Page<StockMovementResponse> getStockMovements(
            Long productId,
            int page,
            int size
    );
}