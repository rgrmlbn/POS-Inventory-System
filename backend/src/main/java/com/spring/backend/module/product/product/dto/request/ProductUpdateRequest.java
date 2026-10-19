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

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price cannot be negative")
    private BigDecimal costPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price cannot be negative")
    private BigDecimal sellingPrice;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;
}
