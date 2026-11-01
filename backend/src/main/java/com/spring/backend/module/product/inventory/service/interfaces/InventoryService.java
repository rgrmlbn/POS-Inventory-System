package com.spring.backend.module.product.inventory.service.interfaces;

import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

public interface InventoryService {

    Page<InventoryResponse> getAllInventory(String productName, int page, int size);

    void createInventory(ProductEntity product);

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse updateProductInventory(Long productId, InventoryUpdateRequest request);
}