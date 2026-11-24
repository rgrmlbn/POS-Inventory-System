package com.spring.backend.module.dashboard.service.impl;

import com.spring.backend.module.dashboard.dto.response.ProductCountResponse;
import com.spring.backend.module.dashboard.mapper.DashboardMapper;
import com.spring.backend.module.dashboard.service.interfaces.DashboardService;
import com.spring.backend.module.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final DashboardMapper dashboardMapper;

    @Override
    public ProductCountResponse getProductCount() {

        Long productCount = productRepository.count();

        return dashboardMapper.toProductCountResponse(productCount);
    }
}
