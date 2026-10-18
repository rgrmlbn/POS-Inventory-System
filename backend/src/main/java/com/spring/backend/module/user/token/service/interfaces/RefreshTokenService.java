package com.spring.backend.module.user.token.service.interfaces;


import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.user.entity.UserEntity;

public interface RefreshTokenService {

    String createRefreshToken(UserEntity user);

    RefreshToken validateRefreshToken(String rawToken);

    String rotateRefreshToken(RefreshToken oldToken);

    void revokeAllByUser(UserEntity user);

    void deleteAllByUser(UserEntity user);
}
