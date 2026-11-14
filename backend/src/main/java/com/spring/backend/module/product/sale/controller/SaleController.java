package com.spring.backend.module.product.sale.controller;

import com.spring.backend.module.product.sale.dto.request.SaleCreateRequest;
import com.spring.backend.module.product.sale.dto.response.SaleResponse;
import com.spring.backend.module.product.sale.service.interfaces.SaleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public ResponseEntity<Page<SaleResponse>> getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(saleService.getAllSales(page, size));
    }

    @GetMapping("/productName")
    public ResponseEntity<Page<SaleResponse>> getAllSalesByProductName(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(saleService.getAllSalesByProductName(productName, page, size));
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleResponse> getSaleById(
            @PathVariable @Positive Long saleId) {

        return ResponseEntity.ok(saleService.getSaleById(saleId));
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(request));
    }
}