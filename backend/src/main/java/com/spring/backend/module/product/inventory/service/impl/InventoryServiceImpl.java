package com.spring.backend.module.product.inventory.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.product.inventory.dto.request.StockAdjustmentRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.dto.response.StockMovementResponse;
import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.product.inventory.mapper.InventoryMapper;
import com.spring.backend.module.product.inventory.repository.InventoryRepository;
import com.spring.backend.module.product.inventory.repository.StockMovementRepository;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryMapper inventoryMapper;


    @Override
    @Transactional(readOnly = true)
    public Page<InventoryResponse> getAllInventory(
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
    @Transactional
    public InventoryResponse createInventory(InventoryCreateRequest request) {

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new DuplicateInventoryException();   // or your own exception
        }

        InventoryEntity inventory = inventoryMapper.toEntity(request, product);
        InventoryEntity saved = inventoryRepository.save(inventory);

        // optional: write the "Initial stock" StockMovementEntity here if quantity > 0

        return inventoryMapper.toResponse(saved);
    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {

        InventoryEntity inventory = inventoryRepository.findInventoryByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    public InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        return null;
    }

    @Override
    public Page<StockMovementResponse> getStockMovements(Long productId, int page, int size) {
        return null;
    }
}
