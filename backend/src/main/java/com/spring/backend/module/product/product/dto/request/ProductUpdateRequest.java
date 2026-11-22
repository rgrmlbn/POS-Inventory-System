package com.spring.backend.module.product.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductUpdateRequest {

    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String productName;

    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    private String sku;

    @Size(min = 3, max = 100, message = "Barcode must be between 3 and 100 characters")
    private String barcode;

    @Min(value = 1, message = "Category ID must be greater than 0")
    private Long categoryId;

    private BigDecimal costPrice;

    private BigDecimal sellingPrice;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @Size(max = 2048, message = "Product image URL must be 2048 characters or fewer")
    @Pattern(
            regexp = "^https://res\\.cloudinary\\.com/[A-Za-z0-9_-]+/image/upload/.+$",
            message = "Product image URL must be a Cloudinary image URL"
    )
    private String imageUrl;

    @Pattern(
            regexp = "^products/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            message = "Product image public ID is invalid"
    )
    private String imagePublicId;

    @AssertTrue(message = "Product image URL and public ID must be provided together")
    public boolean isImageDetailsValid() {
        return (imageUrl == null) == (imagePublicId == null);
    }
}
