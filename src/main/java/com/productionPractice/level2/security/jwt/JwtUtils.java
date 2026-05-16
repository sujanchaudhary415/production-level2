package com.productionPractice.level2.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    // ✅ Extract token from request
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.substring(7).trim();
    }

    // ✅ Generate token (FIXED NAME)
    public String generateToken(UserDetails userDetails) {

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

    // ✅ Extract username
    public String extractUsername(String token) {
        try {
            return parseClaims(token).getPayload().getSubject();
        } catch (Exception e) {
            logger.warn("Failed to extract username from token");
            return null;
        }
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            logger.warn("JWT validation failed: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    // 🔒 Central parser
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}