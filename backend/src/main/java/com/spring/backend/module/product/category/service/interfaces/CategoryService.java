package com.spring.backend.module.product.category.service.interfaces;

import com.spring.backend.module.product.category.dto.request.CategoryCreateRequest;
import com.spring.backend.module.product.category.dto.request.CategoryUpdateRequest;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    Page<CategoryResponse> getAllCategories(String categoryName, int size, int page);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse updateCategoryById(Long id, CategoryUpdateRequest request);

    void deleteCategoryById(Long id);

    void deleteAllCategories();
}
