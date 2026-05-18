package com.productionPractice.level2.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor
public class LoginResult {
    private final AuthResponse authResponse;
    private final ResponseCookie jwtCookie;
    private final ResponseCookie refreshCookie;
}