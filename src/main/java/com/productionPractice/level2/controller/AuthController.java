package com.productionPractice.level2.controller;

import com.productionPractice.level2.security.jwt.JwtUtils;
import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import com.productionPractice.level2.service.AuthService;
import com.productionPractice.level2.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.signin(request);
        String cookieHeaderValue = authResponse.getJwtCookie().toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeaderValue)
                .body(ApiResponse.success(authResponse, "Login successful"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignUpRequest request) {
        String result = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result,"User registered successfully"));
    }

    @GetMapping("/auth/username")
    public ResponseEntity<ApiResponse<String>> currentUserName(Authentication authentication){
        String result=authService.currentUserName(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(result,"User fetched successfully"));
    }

    @GetMapping("/auth/user")
    public ResponseEntity<ApiResponse<AuthResponse>> getUserDetails(Authentication authentication)
    {
        AuthResponse result=authService.getUserDetails(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(result,"User details fetched successfully"));
    }

    @PostMapping("/auth/signout")
    public ResponseEntity<ApiResponse<ResponseCookie>> getSignedOut()
    {
        ResponseCookie cookie=jwtUtils.cleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success( "Logout successful"));

    }
}