package com.spring.backend.module.product.sale.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemResponse {

    private Long productId;
    private String productName;
    private Integer saleQuantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

}
