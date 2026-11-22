package com.spring.backend.module.product.product.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requiresCloudinaryImageUrl() {
        ProductCreateRequest request = createRequest(null, null);

        assertTrue(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    void acceptsCloudinaryImageUrl() {
        ProductCreateRequest request = createRequest(
                "https://res.cloudinary.com/nplpshdr/image/upload/v123/products/item.jpg",
                "products/123e4567-e89b-12d3-a456-426614174000"
        );

        assertFalse(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    void requiresManagedCloudinaryPublicId() {
        ProductCreateRequest request = createRequest(
                "https://res.cloudinary.com/nplpshdr/image/upload/v123/products/item.jpg",
                "other-folder/item"
        );

        assertTrue(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().equals("imagePublicId")));
    }

    private ProductCreateRequest createRequest(String imageUrl, String imagePublicId) {
        return ProductCreateRequest.builder()
                .productName("Test product")
                .sku("TEST-001")
                .barcode("123456789")
                .categoryId(1L)
                .costPrice(BigDecimal.ONE)
                .sellingPrice(BigDecimal.TEN)
                .reorderLevel(0)
                .imageUrl(imageUrl)
                .imagePublicId(imagePublicId)
                .build();
    }
}
