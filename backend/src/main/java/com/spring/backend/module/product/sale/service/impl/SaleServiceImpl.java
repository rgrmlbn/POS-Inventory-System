package com.spring.backend.module.product.sale.service.impl;

import com.spring.backend.module.exception.common.ResourceNotFoundException;
import com.spring.backend.module.product.category.dto.response.CategoryResponse;
import com.spring.backend.module.product.category.entity.CategoryEntity;
import com.spring.backend.module.product.inventory.service.interfaces.InventoryService;
import com.spring.backend.module.product.product.entity.ProductEntity;
import com.spring.backend.module.product.product.repository.ProductRepository;
import com.spring.backend.module.product.sale.dto.request.SaleCreateRequest;
import com.spring.backend.module.product.sale.dto.request.SaleItemRequest;
import com.spring.backend.module.product.sale.dto.response.SaleResponse;
import com.spring.backend.module.product.sale.entity.SaleEntity;
import com.spring.backend.module.product.sale.entity.SaleItemEntity;
import com.spring.backend.module.product.sale.enums.SaleStatus;
import com.spring.backend.module.product.sale.mapper.SaleMapper;
import com.spring.backend.module.product.sale.repository.SaleRepository;
import com.spring.backend.module.product.sale.service.interfaces.SaleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final InventoryService inventoryService;
    private final ProductRepository productRepository;

    @Override
    public Page<SaleResponse> getAllSales(int page, int size){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<SaleEntity> products = saleRepository.findAll(pageable);

        return products.map(SaleEntity -> saleMapper.toResponse(SaleEntity));
    }

    @Override
    public Page<SaleResponse> getAllSalesByProductName(String productName, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<SaleEntity> sales;

        if(productName == null || productName.isBlank()) {
            sales = saleRepository.findAll(pageable);
        } else {
            sales = saleRepository
                    .findDistinctBySaleItemsProductProductNameContainingIgnoreCase(
                        productName.trim(),
                        pageable
                    );
        }

        return sales.map(SaleEntity -> saleMapper.toResponse(SaleEntity));
    }

    @Override
    public SaleResponse getSaleById(Long saleId) {

        SaleEntity sale = saleRepository.findWithItemsById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale"));

        return saleMapper.toResponse(sale);
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleCreateRequest request) {

        SaleEntity sale = saleMapper.toEntity(request);
        sale.setReceiptNumber(generateReceiptNumber());
        sale.setSaleStatus(SaleStatus.COMPLETED);

        BigDecimal subTotal = BigDecimal.ZERO;

        for (SaleItemRequest itemRequest : request.getItems()) {

            ProductEntity product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product"));

            // Checks stock and decrements; throws if insufficient
            inventoryService.decreaseStock(product.getId(), itemRequest.getQuantity());

            SaleItemEntity item = saleMapper.toItemEntity(itemRequest, product, sale);
            sale.getSaleItems().add(item);
            subTotal = subTotal.add(item.getLineTotal());
        }

        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;

        if (discount.compareTo(subTotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed the subtotal.");
        }

        BigDecimal totalAmount = subTotal.subtract(discount);

        if (request.getAmountPaid().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Amount paid is less than the total amount.");
        }

        sale.setSubTotal(subTotal);
        sale.setDiscount(discount);
        sale.setTotalAmount(totalAmount);
        sale.setChangeAmount(request.getAmountPaid().subtract(totalAmount));

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    private String generateReceiptNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RCP-" + date + "-" + random;
    }
}
