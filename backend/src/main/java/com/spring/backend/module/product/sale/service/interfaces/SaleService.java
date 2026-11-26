package com.spring.backend.module.product.sale.service.interfaces;

import com.spring.backend.module.product.sale.dto.request.SaleCreateRequest;
import com.spring.backend.module.product.sale.dto.response.SaleResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface SaleService {

    BigDecimal getSaleTotalAmount();

    List<SaleResponse> getAllSales();

    Page<SaleResponse> getAllSalesByProductName(String productName, int page, int size);

    SaleResponse getSaleById(Long id);

    SaleResponse createSale(SaleCreateRequest request);

}
