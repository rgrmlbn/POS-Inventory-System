package com.spring.backend.module.product.inventory.service.interfaces;

import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    List<InventoryResponse> getAllInventories();

    Page<InventoryResponse> getAllInventoriesByProductName(String productName, int page, int size);

    void createInventory(ProductEntity product);

    InventoryResponse getInventoryById(Long id);

    InventoryResponse updateInventoryById(Long id, InventoryUpdateRequest request);

    void increaseStock(Long productId, int productQuantity);   // used by purchase
    void decreaseStock(Long productId, int productQuantity);   // used by sale, throws if quantity > stock

}