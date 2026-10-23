package com.spring.backend.module.product.category.controller;

import com.spring.backend.module.product.category.dto.request.CategoryCreateRequest;
import com.spring.backend.module.product.category.dto.request.CategoryUpdateRequest;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import com.spring.backend.module.product.category.service.interfaces.CategoryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_READ + "')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(categoryService.getAllCategories(categoryName, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_READ + "')")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable @Positive Long id) {

        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_CREATE + "')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PatchMapping("/{id}/update")
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_UPDATE + "')")
    public ResponseEntity<CategoryResponse> updateCategoryById(@PathVariable @Positive Long id, @RequestBody @Valid CategoryUpdateRequest update) {

        return ResponseEntity.ok(categoryService.updateCategoryById(id, update));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_DELETE + "')")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable @Positive Long id) {

        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAuthority('" + Perm.CATEGORY_DELETE + "')")
    public ResponseEntity<Void> deleteAllCategories() {

        categoryService.deleteAllCategories();
        return ResponseEntity.noContent().build();
    }
}
