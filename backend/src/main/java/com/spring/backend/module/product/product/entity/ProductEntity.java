
package com.spring.backend.module.product.product.entity;

import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.inventory.entity.InventoryEntity;
import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, unique = true)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    private Integer reorderLevel;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private InventoryEntity inventory;
}
