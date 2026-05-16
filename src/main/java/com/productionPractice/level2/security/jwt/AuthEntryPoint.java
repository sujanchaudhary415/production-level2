package com.productionPractice.level2.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productionPractice.level2.wrapper.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);
    private final ObjectMapper objectMapper;

    public AuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.warn("Unauthorized access attempt to secure route: {} - Context: {}",
                request.getRequestURI(),
                authException.getMessage());

        // Setup compliance parameters for down-stream network transport
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // Explicit charset protection
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .success(false)
                .message("Full authentication is required to access this resource.")
                .errorCode("UNAUTHORIZED")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .timestamp(Instant.now().toString())
                .build();

        // Directly pipe bytes to network buffer
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}