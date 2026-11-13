package com.spring.backend.module.product.inventory.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;

import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.product.inventory.mapper.InventoryMapper;
import com.spring.backend.module.product.inventory.repository.InventoryRepository;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAllInventories(int page, int size){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<InventoryEntity> products = inventoryRepository.findAll(pageable);

        return products.map(CategoryEntity -> inventoryMapper.toResponse(CategoryEntity));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAllInventoriesByProductName(
            String productName,
            int page,
            int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<InventoryEntity> inventories;

        if (productName == null || productName.isBlank()) {
            inventories = inventoryRepository.findAll(pageable);
        } else {
            inventories = inventoryRepository
                    .findByProductProductNameContainingIgnoreCase(
                            productName.trim(),
                            pageable);
        }

        return inventories.map(InventoryEntity -> inventoryMapper.toResponse(InventoryEntity));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventory", key = "#productId")
    public InventoryResponse getInventoryByProductId(Long productId) {

        InventoryEntity inventory = inventoryRepository.findInventoryByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#productId")
    public InventoryResponse updateProductInventory(Long productId, InventoryUpdateRequest request) {
        InventoryEntity inventory = inventoryRepository.findInventoryByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        inventory.setProductQuantity(request.getQuantity());
        return inventoryMapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public void createInventory(ProductEntity product) {

        InventoryEntity inventory = InventoryEntity.builder()
                .product(product)
                .productQuantity(0)
                .build();

        inventoryRepository.save(inventory);
    }

    @Override
    public void increaseStock(Long productId, int productQuantity) {

        InventoryEntity inventory = inventoryRepository.findInventoryByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        inventory.setProductQuantity(inventory.getProductQuantity() + productQuantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void decreaseStock(Long productId, int productQuantity) {

        InventoryEntity inventory = inventoryRepository.findInventoryByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        if(inventory.getProductQuantity() < productQuantity) {
            throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
        }

        inventory.setProductQuantity(inventory.getProductQuantity() - productQuantity);
        inventoryRepository.save(inventory);

    }
}
