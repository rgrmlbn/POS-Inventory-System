package com.spring.backend.module.product.sale.dto.request;

import com.spring.backend.module.product.sale.entity.SaleItemEntity;
import com.spring.backend.module.product.sale.enums.PaymentMethod;
import com.spring.backend.module.product.sale.enums.SaleStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SaleCreateRequest {

    @NotEmpty(message = "At least one item is required.")
    @Valid
    private List<SaleItemRequest> items;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative.")
    private BigDecimal discount;          // optional, null treated as 0

    @NotNull(message = "Payment Method is required.")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Amount Paid is required.")
    @DecimalMin(value = "0.00", message = "Amount Paid cannot be negative.")
    private BigDecimal amountPaid;
}
