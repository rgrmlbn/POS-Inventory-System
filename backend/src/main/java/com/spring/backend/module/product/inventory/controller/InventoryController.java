package com.spring.backend.module.product.inventory.controller;

import com.spring.backend.module.product.inventory.dto.request.InventoryUpdateRequest;
import com.spring.backend.module.product.inventory.dto.response.InventoryResponse;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping()
    public ResponseEntity<List<InventoryResponse>> getAllInventories(){

        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/productName")
    public ResponseEntity<Page<InventoryResponse>> getAllInventoriesByProductName(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ){

        return ResponseEntity.ok(inventoryService.getAllInventoriesByProductName(productName, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(
            @PathVariable @Positive Long id){

        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<InventoryResponse> updateInventoryById(
            @PathVariable @Positive Long id,
            @Valid @RequestBody InventoryUpdateRequest request) {

        return ResponseEntity.ok(inventoryService.updateInventoryById(id, request));
    }
}
