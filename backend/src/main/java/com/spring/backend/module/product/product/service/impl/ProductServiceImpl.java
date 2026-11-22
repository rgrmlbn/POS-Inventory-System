package com.spring.backend.module.product.product.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.products.products.DuplicateProductException;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.category.repository.CategoryRepository;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.request.ProductUpdateRequest;
import com.spring.backend.module.product.product.dto.response.ProductDetailedResponse;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.product.mapper.ProductMapper;
import com.spring.backend.module.product.product.repository.ProductRepository;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductEntity -> productMapper.toResponse(ProductEntity))
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsByProductName(
            String productName,
            int page,
            int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> products;

        if (productName == null || productName.isBlank()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository
                    .findAllByProductNameContainingIgnoreCase(
                            productName.trim(),
                            pageable
                    );
        }

        return products.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "product", key = "#id")
    public ProductDetailedResponse getProductById(Long id) {

        ProductEntity product = productRepository.findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        return productMapper.toDetailedResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductBySku(String sku, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllBySkuContainingIgnoreCase(sku, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsByCategoryId(Long categoryId, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllByCategoryId(categoryId, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        ProductEntity product = productMapper.toEntity(request, category);

        ProductEntity savedProduct = productRepository.save(product);

        inventoryService.createInventory(savedProduct);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
//    @CachePut(value = "product", key = "#id")
    public ProductDetailedResponse updateProductById(Long id, ProductUpdateRequest update) {

        ProductEntity product = productRepository.findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        if (update.getProductName() != null && !update.getProductName().isBlank()) {
            product.setProductName(update.getProductName());
        }

        if (update.getSku() != null && !update.getSku().isBlank()) {
            if (!product.getSku().equals(update.getSku())
                    && productRepository.existsBySku(update.getSku())) {
                throw new DuplicateProductException();
            }
            product.setSku(update.getSku());
        }

        if(update.getBarcode() != null && !update.getBarcode().isBlank()) {
            if(!product.getBarcode().equals(update.getBarcode())
                    && productRepository.existsByBarcode(update.getBarcode())) {
                throw new DuplicateProductException();
            }
            product.setBarcode(update.getBarcode());
        }

        if(update.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(update.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category"));
            product.setCategory(category);
        }

        if(update.getCostPrice() != null) {
            product.setCostPrice(update.getCostPrice());
        }

        if(update.getSellingPrice()!= null) {
            product.setSellingPrice(update.getSellingPrice());
        }

        if(update.getReorderLevel()!= null) {
            product.setReorderLevel(update.getReorderLevel());
        }

        if (update.getImageUrl() != null && !update.getImageUrl().isBlank()) {
            product.setImageUrl(update.getImageUrl());
        }

        return productMapper.toDetailedResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "inventory", key = "#id")
    })
    public void deleteProductById(Long id) {

        ProductEntity product = productRepository.findProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        productRepository.delete(product);

    }

    @Override
    @Transactional
    @CacheEvict(value = "product", allEntries = true)
    public void deleteAllProducts() {

        productRepository.deleteAll();
    }
}
