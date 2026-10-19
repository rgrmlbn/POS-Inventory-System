package com.spring.backend.module.product.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String productName;
    private String sku;
    private String barcode;
    private String categoryName;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer reorderLevel;
    private Boolean isActive;
}

