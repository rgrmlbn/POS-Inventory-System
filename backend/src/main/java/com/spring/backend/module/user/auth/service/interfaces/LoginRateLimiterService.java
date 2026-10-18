package com.spring.backend.module.user.auth.service.interfaces;

public interface LoginRateLimiterService {
    void checkLimits(String email);
}