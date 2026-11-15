package com.spring.backend.module.product.purchase.controller;

import com.spring.backend.module.product.purchase.dto.request.PurchaseCreateRequest;
import com.spring.backend.module.product.purchase.dto.response.PurchaseResponse;
import com.spring.backend.module.product.purchase.service.interfaces.PurchaseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Validated
public class PurchaseController {

    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<Page<PurchaseResponse>> getAllPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(purchaseService.getAllPurchases(page, size));
    }

    @GetMapping("/productName")
    public ResponseEntity<Page<PurchaseResponse>> getAllPurchasesByProductName(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(purchaseService.getAllPurchasesByProductName(productName, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseByProductId(
            @PathVariable @Positive Long id){

        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<PurchaseResponse> createPurchase(
            @RequestBody @Valid PurchaseCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.createPurchase(request));
    }

}
