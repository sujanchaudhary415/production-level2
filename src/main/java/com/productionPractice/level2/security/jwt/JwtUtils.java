package com.productionPractice.level2.security.jwt;

import com.productionPractice.level2.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    @Value("${spring.app.jwtRefreshCookie}")
    private String jwtRefreshCookie;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        validateSecret(jwtSecret);
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        logger.info("JWT key initialized successfully");
    }

    private void validateSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be empty");
        }
        if (secret.length() < 44) {
            throw new IllegalArgumentException("JWT secret must be at least 44 chars (256-bit)");
        }
    }

    // ✅ FIXED: Now properly returns the cookie value back to the filter
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        return cookie != null ? cookie.getValue() : null;
    }

    // ✅ FIXED: Hardened security settings by enabling httpOnly(true)
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateAccessToken(userPrincipal);
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(jwtExpirationMs / 1000) // Converts milliseconds to seconds for the cookie lifespan
                .httpOnly(true)                 // Prevents cross-site scripting (XSS) token theft
                .secure(false)                  // Toggle to true when deploying on an HTTPS environment
                .sameSite("Lax")                // Balanced CSRF defense mechanism
                .build();
    }

    public ResponseCookie cleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .maxAge(0)
                .build();
    }

    public String generateAccessToken(UserDetailsImpl userDetails) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return parseClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            logger.warn("Failed to extract username from token");
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            logger.warn("JWT validation failed: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }



    // 1. Get Refresh Token from Cookies
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookie);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // 2. Generate Secure Refresh Token Cookie (Scoped strictly to /api/auth/refresh)
    public ResponseCookie generateRefreshCookie(String refreshToken) {
        return ResponseCookie.from(jwtRefreshCookie, refreshToken)
                .path("/api/auth/refresh") // CRITICAL: Browser only sends this cookie to the refresh endpoint
                .maxAge(jwtRefreshExpirationMs / 1000)
                .httpOnly(true)
                .secure(false) // Set to true in your production HTTPS environment
                .sameSite("Lax")
                .build();
    }

    // 3. Clean Cookie Utilities for Logout Action
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").maxAge(0).build();
    }

    public ResponseCookie getCleanRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null).path("/api/auth/refresh").maxAge(0).build();
    }
}