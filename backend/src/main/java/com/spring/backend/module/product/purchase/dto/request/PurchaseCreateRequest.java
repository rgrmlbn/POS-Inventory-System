package com.spring.backend.module.product.purchase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Integer purchaseQuantity;

    @NotNull(message = "Unit Cost is required.")
    private BigDecimal unitCost;

    @NotBlank(message = "Supplier Name quantity is required.")
    private BigDecimal supplierName;

    private String note;

}
