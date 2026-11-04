package com.spring.backend.module.product.sale.mapper;

import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.sale.dto.request.SaleCreateRequest;
import com.spring.backend.module.product.sale.dto.request.SaleItemRequest;
import com.spring.backend.module.product.sale.dto.response.SaleItemResponse;
import com.spring.backend.module.product.sale.dto.response.SaleResponse;
import com.spring.backend.module.product.sale.entity.SaleEntity;
import com.spring.backend.module.product.sale.entity.SaleItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SaleMapper {

    public SaleEntity toEntity(SaleCreateRequest request) {
        return SaleEntity.builder()
                .discount(request.getDiscount())
                .amountPaid(request.getAmountPaid())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public SaleItemEntity toItemEntity(SaleItemRequest request, ProductEntity product, SaleEntity sale) {
        BigDecimal lineTotal = product.getSellingPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        return SaleItemEntity.builder()
                .sale(sale)
                .product(product)
                .saleQuantity(request.getQuantity())
                .unitPrice(product.getSellingPrice())
                .unitCost(product.getCostPrice())
                .lineTotal(lineTotal)
                .build();
    }

    public SaleResponse toResponse(SaleEntity sale) {
        List<SaleItemResponse> items = sale.getSaleItems().stream()
                .map(this::toItemResponse)      // fixed: was shadowing `items`
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .receiptNumber(sale.getReceiptNumber())
                .subtotal(sale.getSubTotal())
                .discount(sale.getDiscount())
                .totalAmount(sale.getTotalAmount())
                .amountPaid(sale.getAmountPaid())
                .changeAmount(sale.getChangeAmount())
                .paymentMethod(sale.getPaymentMethod())
                .status(sale.getSaleStatus())
                .createdAt(sale.getCreatedAt())   // use your BaseEntity's field name
                .items(items)
                .build();
    }

    public SaleItemResponse toItemResponse(SaleItemEntity item) {

        return SaleItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .saleQuantity(item.getSaleQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
