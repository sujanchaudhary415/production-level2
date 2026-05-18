package com.productionPractice.level2.service;

import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse signin(LoginRequest request);
    String signup(SignUpRequest request);
    String currentUserName(Authentication authentication);
    AuthResponse getUserDetails(Authentication authentication);
}
