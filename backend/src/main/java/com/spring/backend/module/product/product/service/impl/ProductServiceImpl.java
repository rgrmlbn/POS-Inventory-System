package com.spring.backend.module.product.product.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.exception.products.products.DuplicateProductException;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.request.ProductUpdateRequest;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.product.mapper.ProductMapper;
import com.spring.backend.module.product.product.repository.ProductRepository;
import com.spring.backend.module.product.product.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public Page<ProductResponse> getAllProducts(String productName, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllByProductNameContainingIgnoreCase(productName, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

//    @Override
//    public ProductResponse getProductById(Long id) {
//
//        ProductEntity product = productRepository.findProductWithCategory(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product"));
//
//        return productMapper.toResponse(product);
//    }
//
//    @Override
//    public Page<ProductResponse> getProductBySku(String sku, int page, int size) {
//
//        Pageable pageable = Pageable.ofSize(size).withPage(page);
//
//        Page<ProductEntity> product = productRepository.findAllSkuContainingIgnoreCase(sku, pageable);
//
//        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
//    }

    @Override
    public Page<ProductResponse> getProductByCategoryId(Long categoryId, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ProductEntity> product = productRepository.findAllByCategoryId(categoryId, pageable);

        return product.map(ProductEntity -> productMapper.toResponse(ProductEntity));
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request, CategoryEntity category) {

        ProductEntity product = productMapper.toEntity(request, category);

        ProductEntity savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest update) {

        ProductEntity product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        if(update.getProductName() != null && !update.getProductName().isBlank()){
            product.setProductName(update.getProductName());
        }

        if(update.getSku() != null && !update.getSku().isBlank()){

            if(product.getSku().equals(update.getSku())){
                throw new DuplicateProductException();
            }

            product.setSku(update.getSku());
        }

        return null;
    }

    @Override
    public void deleteProduct(Long productId) {

        ProductEntity product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product"));

        productRepository.deleteById(productId);

    }

    @Override
    public void deleteAllProducts() {

        productRepository.deleteAll();
    }
}
