package com.spring.backend.module.user.token.service.interfaces;

public interface TokenBlacklistService {
    void blacklist(String token);
    boolean isBlacklisted(String token);
}