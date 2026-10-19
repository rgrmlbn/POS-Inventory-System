package com.spring.backend.module.product.category.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryUpdateRequest {

    @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
    private String categoryName;
}
