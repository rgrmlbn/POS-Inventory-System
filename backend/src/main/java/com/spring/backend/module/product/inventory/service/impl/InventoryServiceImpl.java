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

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Long getTotalProductQuantity() {
        return inventoryRepository.sumProductQuantity().orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        return inventoryRepository.sumInventoryValue().orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories(){

        return inventoryRepository.findAll().stream()
                .map(InventoryEntity -> inventoryMapper.toResponse(InventoryEntity))
                .toList();

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
    @Cacheable(value = "inventory", key = "#id")
    public InventoryResponse getInventoryById(Long id) {

        InventoryEntity inventory = inventoryRepository.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory"));

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#id")
    public InventoryResponse updateInventoryById(Long id, InventoryUpdateRequest request) {
        InventoryEntity inventory = inventoryRepository.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory"));

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
    @Transactional
    public void increaseStock(Long productId, int productQuantity) {

        if (productQuantity <= 0) {
            throw new IllegalArgumentException("Stock quantity must be greater than zero.");
        }

        InventoryEntity inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory"));

        inventory.setProductQuantity(inventory.getProductQuantity() + productQuantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, int productQuantity) {

        if (productQuantity <= 0) {
            throw new IllegalArgumentException("Stock quantity must be greater than zero.");
        }

        InventoryEntity inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory"));

        if(inventory.getProductQuantity() < productQuantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + inventory.getProduct().getProductName());
        }

        inventory.setProductQuantity(inventory.getProductQuantity() - productQuantity);
        inventoryRepository.save(inventory);

    }
}
