package com.spring.backend.module.product.product.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.products.products.DuplicateProductException;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.category.repository.CategoryRepository;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.request.ProductUpdateRequest;
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

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("productPagesAll")
    public Page<ProductResponse> getAllProducts(
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

        return products.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#productId")
    public ProductResponse getProductById(Long productId) {

        ProductEntity product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("productPagesSku")
    public Page<ProductResponse> getProductBySku(String sku, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllBySkuContainingIgnoreCase(sku, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("productPagesCategory")
    public Page<ProductResponse> getAllProductsByCategoryId(Long categoryId, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllByCategoryId(categoryId, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"productPagesAll", "productPagesSku", "productPagesCategory"}, allEntries = true)
    public ProductResponse createProduct(ProductCreateRequest request) {

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        ProductEntity product = productMapper.toEntity(request, category);

        ProductEntity savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "product", key = "#productId"),
            evict = @CacheEvict(cacheNames = {"productPagesAll", "productPagesSku", "productPagesCategory"}, allEntries = true)
    )
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest update) {

        ProductEntity product = productRepository.findProductById(productId)
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

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(cacheNames = {"productPagesAll", "productPagesSku", "productPagesCategory"}, allEntries = true)
    })
    public void deleteProductById(Long productId) {

        ProductEntity product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        productRepository.deleteById(productId);

    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", allEntries = true),
            @CacheEvict(cacheNames = {"productPagesAll", "productPagesSku", "productPagesCategory"}, allEntries = true)
    })
    public void deleteAllProducts() {

        productRepository.deleteAll();
    }
}
