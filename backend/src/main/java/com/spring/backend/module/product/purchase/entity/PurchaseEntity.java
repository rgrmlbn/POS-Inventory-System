package com.spring.backend.module.product.purchase.entity;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_purchases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer purchaseQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private Double unitCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private Double totalCost;   // purchaseQuantity * unitCost, set by the service

    @Column(nullable = false)
    private String supplierName;
    private String note;
}
