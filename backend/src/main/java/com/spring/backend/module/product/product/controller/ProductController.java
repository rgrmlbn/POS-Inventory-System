package com.spring.backend.module.product.product.controller;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.request.ProductUpdateRequest;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import com.spring.backend.module.security.permission.Perm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;


    @GetMapping
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_READ + "')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(productService.getAllProducts(productName, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_READ + "')")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable @Positive Long productId){

        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/sku")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_READ + "')")
    public ResponseEntity<Page<ProductResponse>> getProductBySku(
            @RequestParam(required = false) String sku,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(productService.getProductBySku(sku, page, size));
    }

    @GetMapping("/category/{id}")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_READ + "')")
    public ResponseEntity<Page<ProductResponse>>getAllProductsByCategoryId(
            @PathVariable @Positive Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(productService.getAllProductsByCategoryId(categoryId, page, size));

    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_CREATE + "')")
    public ResponseEntity<ProductResponse> createProduct(
          @RequestBody @Valid ProductCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_UPDATE + "')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable @Positive Long productId,
            @RequestBody @Valid ProductUpdateRequest update){

        return ResponseEntity.ok(productService.updateProduct(productId, update));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_DELETE + "')")
    public ResponseEntity<Void> deleteProductById(
            @PathVariable @Positive Long productId){

        productService.deleteProductById(productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('" + Perm.PRODUCT_DELETE + "')")
    public ResponseEntity<Void> deleteAllProducts(){

        productService.deleteAllProducts();

        return ResponseEntity.noContent().build();
    }
}
