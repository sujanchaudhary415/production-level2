package com.productionPractice.level2.controller;

import com.productionPractice.level2.security.response.LoginResult;
import com.productionPractice.level2.security.jwt.JwtUtils;
import com.productionPractice.level2.security.request.LoginRequest;
import com.productionPractice.level2.security.request.SignUpRequest;
import com.productionPractice.level2.security.response.AuthResponse;
import com.productionPractice.level2.security.services.UserDetailsImpl;
import com.productionPractice.level2.service.AuthService;
import com.productionPractice.level2.service.Impl.RefreshTokenServiceImpl;
import com.productionPractice.level2.wrapper.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RefreshTokenServiceImpl refreshTokenService;
    private final JwtUtils jwtUtils;

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(@Valid @RequestBody LoginRequest request) {
        LoginResult result = authService.signin(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, result.getJwtCookie().toString());
        headers.add(HttpHeaders.SET_COOKIE, result.getRefreshCookie().toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(result.getAuthResponse(), "Login successful"));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<String>> refreshCookie(HttpServletRequest request) {
        String tokenFromCookie = jwtUtils.getJwtRefreshFromCookies(request);
        ResponseCookie freshAccessCookie = authService.refreshAccessToken(tokenFromCookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, freshAccessCookie.toString())
                .body(ApiResponse.success("Access token refreshed successfully"));
    }

    @PostMapping("/auth/signout")
    public ResponseEntity<ApiResponse<String>> getSignedOut(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            refreshTokenService.deleteByUserId(userDetails.getId());
        }

        ResponseCookie cleanJwt = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefresh = jwtUtils.getCleanRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwt.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefresh.toString())
                .body(ApiResponse.success("Logout successful"));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignUpRequest request) {
        String result = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "User registered successfully"));
    }

    @GetMapping("/auth/username")
    public ResponseEntity<ApiResponse<String>> currentUserName(Authentication authentication) {
        String result = authService.currentUserName(authentication);
        return ResponseEntity.ok().body(ApiResponse.success(result, "User fetched successfully"));
    }

    @GetMapping("/auth/user")
    public ResponseEntity<ApiResponse<AuthResponse>> getUserDetails(Authentication authentication) {
        AuthResponse result = authService.getUserDetails(authentication);
        return ResponseEntity.ok().body(ApiResponse.success(result, "User details fetched successfully"));
    }
}