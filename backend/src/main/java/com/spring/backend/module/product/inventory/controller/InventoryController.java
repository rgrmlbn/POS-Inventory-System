package com.spring.backend.module.product.inventory.controller;

import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping()
    public ResponseEntity<Page<InventoryResponse>> getAllInventories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ){

        return ResponseEntity.ok(inventoryService.getAllInventories(page, size));
    }

    @GetMapping("/productName")
    public ResponseEntity<Page<InventoryResponse>> getAllInventoriesByProductName(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ){

        return ResponseEntity.ok(inventoryService.getAllInventoriesByProductName(productName, page, size));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable @Positive Long productId){

        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping("/{productId}/update")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable @Positive Long productId,
            @RequestBody InventoryUpdateRequest request){
        // Implementation for updating inventory
        return ResponseEntity.ok(inventoryService.updateProductInventory(productId, request));
    }
}
