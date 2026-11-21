package com.spring.backend.module.product.purchase.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PurchaseCreateRequest {

    @NotNull(message = "Product ID is required.")
    private Long productId;

    @NotNull(message = "Purchase quantity is required.")
    @Positive(message = "Purchase quantity must be greater than zero.")
    private Integer purchaseQuantity;

    @NotNull(message = "Unit Cost is required.")
    @Positive(message = "Unit cost must be greater than zero.")
    private BigDecimal unitCost;

    @NotBlank(message = "Supplier Name quantity is required.")
    private String supplierName;

    private String note;

}
