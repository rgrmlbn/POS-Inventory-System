package com.spring.backend.module.product.inventory.entity;

import com.spring.backend.module.product.inventory.enums.StockMovementType;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType movementType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer previousQuantity;

    @Column(nullable = false)
    private Integer newQuantity;

    @Column(length = 255)
    private String reason;
}
