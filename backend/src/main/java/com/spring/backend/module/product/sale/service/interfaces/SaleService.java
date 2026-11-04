package com.spring.backend.module.product.sale.service.interfaces;

import com.spring.backend.module.product.sale.dto.request.SaleCreateRequest;
import com.spring.backend.module.product.sale.dto.response.SaleResponse;
import org.springframework.data.domain.Page;

public interface SaleService {

    Page<SaleResponse> getAllSales(String saleName, int page, int size);

    SaleResponse getSaleById(Long saleId);

    SaleResponse createSale(SaleCreateRequest request);

}
