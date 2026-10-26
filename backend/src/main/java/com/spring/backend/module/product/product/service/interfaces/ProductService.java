package com.spring.backend.module.product.product.service.interfaces;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.product.dto.request.ProductCreateRequest;
import com.spring.backend.module.product.product.dto.request.ProductUpdateRequest;
import com.spring.backend.module.product.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAllProducts(String productName, int page, int size);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getProductBySku(String sku, int page, int size);

    Page<ProductResponse> getAllProductsByCategoryId(Long categoryId, int page, int size);

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse updateProduct(Long id, ProductUpdateRequest update);

    void deleteProductById(Long id);

    void deleteAllProducts();
}
