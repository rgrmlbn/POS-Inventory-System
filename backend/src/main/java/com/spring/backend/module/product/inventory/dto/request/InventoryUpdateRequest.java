package com.spring.backend.module.product.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InventoryUpdateRequest {

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}