package com.spring.backend.module.product.purchase.service.interfaces;

import com.spring.backend.module.product.purchase.dto.request.PurchaseCreateRequest;
import com.spring.backend.module.product.purchase.dto.response.PurchaseResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface PurchaseService {

    BigDecimal getPurchaseTotalCost();

    List<PurchaseResponse> getAllPurchases();

    Page<PurchaseResponse> getAllPurchasesByProductName(String productName, int page, int size);

    PurchaseResponse getPurchaseById(Long id);

    PurchaseResponse createPurchase(PurchaseCreateRequest request);

}
