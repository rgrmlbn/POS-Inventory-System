package com.spring.backend.module.product.product.controller;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(productService.getAllProducts(productName, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(Long productId){

        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/sku")
    public ResponseEntity<Page<ProductResponse>> getProductBySku(
            @RequestParam(required = false) String sku,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(productService.getProductBySku(sku, page, size));
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Page<ProductResponse>>getAllProductsByCategoryId(
            @Positive Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){

        return ResponseEntity.ok(productService.getAllProductsByCategoryId(categoryId, page, size));

    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(
            ProductCreateRequest request, CategoryEntity category
    )


}
