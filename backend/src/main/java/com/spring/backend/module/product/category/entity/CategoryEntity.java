package com.spring.backend.module.product.category.entity;

import com.spring.backend.module.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    String categoryName;
}
