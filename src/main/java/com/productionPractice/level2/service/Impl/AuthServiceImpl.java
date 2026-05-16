package com.productionPractice.level2.service.Impl;

import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import com.productionPractice.level2.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signin(LoginRequest request) {
        return null;
    }
}
