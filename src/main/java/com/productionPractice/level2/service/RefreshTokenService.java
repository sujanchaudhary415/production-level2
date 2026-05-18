package com.productionPractice.level2.service;

import com.productionPractice.level2.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);
}
