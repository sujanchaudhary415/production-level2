package com.productionPractice.level2.service;

import com.productionPractice.level2.security.response.LoginResult;
import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResult signin(LoginRequest request);
    ResponseCookie refreshAccessToken(String refreshTokenFromCookie);
    AuthResponse signup(SignUpRequest request);
    String currentUserName(Authentication authentication);
    AuthResponse getUserDetails(Authentication authentication);
}