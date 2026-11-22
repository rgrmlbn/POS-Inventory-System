
package com.spring.backend.module.product.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String productName;

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    private String sku;

    @NotBlank(message = "Barcode is required")
    @Size(min = 3, max = 100, message = "Barcode must be between 3 and 100 characters")
    private String barcode;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Category ID must be greater than 0")
    private Long categoryId;

    @NotNull(message = "Cost price is required")
    private BigDecimal costPrice;

    @NotNull(message = "Selling price is required")
    private BigDecimal sellingPrice;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @NotBlank(message = "Product image URL is required")
    @Size(max = 2048, message = "Product image URL must be 2048 characters or fewer")
    @Pattern(
            regexp = "^https://res\\.cloudinary\\.com/[A-Za-z0-9_-]+/image/upload/.+$",
            message = "Product image URL must be a Cloudinary image URL"
    )
    private String imageUrl;
}
