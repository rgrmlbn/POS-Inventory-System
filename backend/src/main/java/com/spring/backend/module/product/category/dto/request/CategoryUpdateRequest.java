package com.spring.backend.module.product.category.dto.request;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryUpdateRequest {

    @Min(value = 3, message = "Category name must be at least 3 characters long")
    private String categoryName;
}
