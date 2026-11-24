package com.spring.backend.module.dashboard.mapper;

import com.spring.backend.module.dashboard.dto.response.ProductCountResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {
    public ProductCountResponse toProductCountResponse(Long productCount) {
        return ProductCountResponse.builder()
                .productCount(productCount)
                .build();
    }
}
