package com.spring.backend.module.product.category.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.products.category.DuplicateCategoryException;
import com.spring.backend.module.product.category.dto.request.CategoryCreateRequest;
import com.spring.backend.module.product.category.dto.request.CategoryUpdateRequest;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.category.mapper.CategoryMapper;
import com.spring.backend.module.product.category.repository.CategoryRepository;
import com.spring.backend.module.product.category.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("categoryPagesAll")
    public Page<CategoryResponse> getAllCategories(String categoryName, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<CategoryEntity> categories;

        if (categoryName == null || categoryName.isBlank()) {
            categories = categoryRepository.findAll(pageable);
        } else {
            categories = categoryRepository
                    .findAllByCategoryNameContainingIgnoreCase(
                            categoryName.trim(),
                            pageable
                    );
        }

        return categories.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "category", key = "#id")
    public CategoryResponse getCategoryById(Long id) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categoryPagesAll", allEntries = true)
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateCategoryException();
        }

        CategoryEntity category = categoryMapper.toEntity(request);

        CategoryEntity savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "category", key = "#id"),
            evict = @CacheEvict(
                    cacheNames = {"categoryPagesAll", "product", "productPagesAll", "productPagesSku", "productPagesCategory"},
                    allEntries = true)
    )
    public CategoryResponse updateCategoryById(Long id, CategoryUpdateRequest request) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {

            // Only check for duplicates when the name actually changes
            if (!request.getCategoryName().equals(category.getCategoryName())
                    && categoryRepository.existsByCategoryName(request.getCategoryName())) {
                throw new DuplicateCategoryException();
            }

            category.setCategoryName(request.getCategoryName());
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#id"),
            @CacheEvict(
                    cacheNames = {"categoryPagesAll", "product", "productPagesAll", "productPagesSku", "productPagesCategory"},
                    allEntries = true)
    })
    public void deleteCategoryById(Long id) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "category", allEntries = true),
            @CacheEvict(
                    cacheNames = {"categoryPagesAll", "product", "productPagesAll", "productPagesSku", "productPagesCategory"},
                    allEntries = true)
    })
    public void deleteAllCategories() {

        categoryRepository.deleteAll();
    }
}