package com.spring.backend.module.product.sale.entity;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_sale_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private SaleEntity sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer saleQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;    // snapshot of product.sellingPrice

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;     // snapshot of product.costPrice

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;    // unitPrice * saleQuantity
}
