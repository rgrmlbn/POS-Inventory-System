package com.spring.backend.module.product.purchase.service.interfaces;

import com.spring.backend.module.product.purchase.dto.request.PurchaseCreateRequest;
import com.spring.backend.module.product.purchase.dto.response.PurchaseResponse;
import org.springframework.data.domain.Page;

public interface PurchaseService {

    Page<PurchaseResponse> getAllPurchases(int page, int size);

    Page<PurchaseResponse> getAllPurchasesByProductName(String productName, int page, int size);

    PurchaseResponse getPurchaseById(Long id);

    PurchaseResponse createPurchase(PurchaseCreateRequest request);
}
