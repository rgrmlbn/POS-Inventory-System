package com.spring.backend.module.product.inventory.dto.request;

import com.spring.backend.module.product.inventory.enums.StockMovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StockAdjustmentRequest {

    @NotNull(message = "Movement type is required.")
    private StockMovementType movementType;

    @NotNull(message = "Quantity is required.")
    private Integer quantity;

    @NotBlank(message = "Reason is required.")
    private String reason;
}
