package com.spring.backend.module.product.purchase.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.product.repository.ProductRepository;
import com.spring.backend.module.product.purchase.dto.request.PurchaseCreateRequest;
import com.spring.backend.module.product.purchase.dto.response.PurchaseResponse;
import com.spring.backend.module.product.purchase.entity.PurchaseEntity;
import com.spring.backend.module.product.purchase.mapper.PurchaseMapper;
import com.spring.backend.module.product.purchase.repository.PurchaseRepository;
import com.spring.backend.module.product.purchase.service.interfaces.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final  ProductRepository productRepository;
    private final  PurchaseMapper purchaseMapper;
    private final  InventoryService inventoryService;

    @Override
    public BigDecimal getTotalPurchaseCost() {
        return purchaseRepository.sumPurchaseCost().orElse(BigDecimal.ZERO);
    }

    @Override
    public List<PurchaseResponse> getAllPurchases() {

        return purchaseRepository.findAll().stream()
                .map(PurchaseEntity -> purchaseMapper.toResponse(PurchaseEntity))
                .toList();
    }

    @Override
    public Page<PurchaseResponse> getAllPurchasesByProductName(
            String productName,
            int page,
            int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<PurchaseEntity> purchases;

        if(productName == null || productName.isBlank()) {
            purchases = purchaseRepository.findAll(pageable);
        } else {
            purchases = purchaseRepository.findByProductProductNameContainingIgnoreCase(
                    productName.trim(),
                    pageable
            );
        }

        return purchases.map(PurchaseEntity -> purchaseMapper.toResponse(PurchaseEntity));
    }

    @Override
    public PurchaseResponse getPurchaseById(Long id) {

        PurchaseEntity purchase = purchaseRepository.findPurchaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase"));

        return purchaseMapper.toResponse(purchase);
    }

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseCreateRequest request) {

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        inventoryService.increaseStock(product.getId(), request.getPurchaseQuantity());

        PurchaseEntity purchase = purchaseMapper.toEntity(request, product);

        purchase.setTotalCost(
                purchase.getUnitCost().multiply(
                        BigDecimal.valueOf(purchase.getPurchaseQuantity())
                )
        ); // set total cost

        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(savedPurchase);
    }
}
