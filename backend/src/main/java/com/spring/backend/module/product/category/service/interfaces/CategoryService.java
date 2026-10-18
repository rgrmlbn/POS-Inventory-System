package com.spring.backend.module.product.category.service.interfaces;

import com.spring.backend.module.product.category.dto.request.CategoryCreateRequest;
import com.spring.backend.module.product.category.dto.request.CategoryUpdateRequest;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse updateCategoryById(Long id, CategoryUpdateRequest request);

    void deleteCategoryById(Long id);

    void deleteAllCategories();
}
