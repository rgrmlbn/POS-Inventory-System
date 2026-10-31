package com.spring.backend.module.product.inventory.controller;

import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> getAllInventory(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ){

        return ResponseEntity.ok(inventoryService.getAllInventory(productName, page, size));
    }
}
