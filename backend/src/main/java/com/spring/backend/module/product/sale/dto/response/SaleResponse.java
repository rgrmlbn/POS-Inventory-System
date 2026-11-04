package com.spring.backend.module.product.sale.dto.response;

import com.spring.backend.module.product.sale.enums.PaymentMethod;
import com.spring.backend.module.product.sale.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private Long id;
    private String receiptNumber;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private Instant createdAt;       // match your BaseEntity field/type
    private List<SaleItemResponse> items;

}
