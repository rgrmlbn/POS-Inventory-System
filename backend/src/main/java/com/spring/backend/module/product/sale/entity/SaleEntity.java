package com.spring.backend.module.product.sale.entity;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.sale.enums.PaymentMethod;
import com.spring.backend.module.product.sale.enums.SaleStatus;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_sales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal changeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;     // CASH, CARD, EWALLET

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus saleStatus;               // COMPLETED, VOIDED

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItemEntity> saleItems = new ArrayList<>();
}
