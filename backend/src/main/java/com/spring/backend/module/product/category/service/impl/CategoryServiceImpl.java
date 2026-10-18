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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> categoryMapper.toResponse(category))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

                return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        if(categoryRepository.existsByName(request.getCategoryName())){
            throw new DuplicateCategoryException();
        }

        CategoryEntity category = categoryMapper.toEntity(request);

        categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategoryById(Long id, CategoryUpdateRequest request) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        if(request.getCategoryName() != null && !request.getCategoryName().isBlank()){

            if(categoryRepository.existsByName(request.getCategoryName())){
                throw new DuplicateCategoryException();
            }

            category.setCategoryName(request.getCategoryName());

        }

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {

        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        categoryRepository.delete(category);

    }

    @Override
    @Transactional
    public void deleteAllCategories() {

        categoryRepository.deleteAll();

    }
}
