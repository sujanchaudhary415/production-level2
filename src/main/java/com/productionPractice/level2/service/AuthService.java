package com.productionPractice.level2.service;

import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;

public interface AuthService {
    AuthResponse signin(LoginRequest request);
    String signup(SignUpRequest request);
}
