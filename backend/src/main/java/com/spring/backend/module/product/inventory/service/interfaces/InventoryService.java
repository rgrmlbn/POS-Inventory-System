package com.spring.backend.module.product.inventory.service.interfaces;

import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

public interface InventoryService {

    Page<InventoryResponse> getAllInventory(int page, int size);

    Page<InventoryResponse> getAllInventoryByProductName(String productName, int page, int size);

    void createInventory(ProductEntity product);

    InventoryResponse getInventoryByProductId(Long productId);

    InventoryResponse updateProductInventory(Long productId, InventoryUpdateRequest request);

    void increaseStock(Long productId, int productQuantity);   // used by purchase
    void decreaseStock(Long productId, int productQuantity);   // used by sale, throws if quantity > stock

}