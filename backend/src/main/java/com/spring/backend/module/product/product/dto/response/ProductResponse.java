package com.spring.backend.module.product.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String productName;
    private String categoryName;
    private BigDecimal sellingPrice;
    private Integer availableQuantity;
}