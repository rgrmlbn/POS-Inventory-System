package com.spring.backend.module.dashboard.controller;

import com.spring.backend.module.dashboard.dto.response.ProductCountResponse;
import com.spring.backend.module.dashboard.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/product-count")
    public ResponseEntity<ProductCountResponse> getProductCount() {
        return ResponseEntity.ok(dashboardService.getProductCount());
    }
}
