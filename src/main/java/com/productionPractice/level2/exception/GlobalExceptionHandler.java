package com.productionPractice.level2.exception;

import com.productionPractice.level2.wrapper.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================
    // VALIDATION ERROR
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, List<String>> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors
                    .computeIfAbsent(error.getField(), k -> new java.util.ArrayList<>())
                    .add(error.getDefaultMessage());
        });

        ApiResponse<Map<String, List<String>>> response =
                ApiResponse.<Map<String, List<String>>>builder()
                        .success(false)
                        .message("Validation Failed")
                        .data(validationErrors)
                        .errorCode("VALIDATION_ERROR")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI())
                        .timestamp(java.time.Instant.now().toString())
                        .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // =========================
    // RESOURCE NOT FOUND
    // =========================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errorCode("NOT_FOUND")
                        .status(HttpStatus.NOT_FOUND.value())
                        .path(request.getRequestURI())
                        .timestamp(java.time.Instant.now().toString())
                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // =========================
    // DUPLICATE ERROR
    // =========================
    @ExceptionHandler(DuplicateErrorException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(
            DuplicateErrorException ex,
            HttpServletRequest request) {

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errorCode("DUPLICATE_ERROR")
                        .status(HttpStatus.CONFLICT.value())
                        .path(request.getRequestURI())
                        .timestamp(java.time.Instant.now().toString())
                        .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // =========================
    // BUSINESS RULE ERROR
    // =========================
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(
            BusinessRuleException ex,
            HttpServletRequest request) {

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errorCode("BUSINESS_RULE_VIOLATION")
                        .status(HttpStatus.CONFLICT.value())
                        .path(request.getRequestURI())
                        .timestamp(java.time.Instant.now().toString())
                        .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // =========================
    // GENERIC ERROR (IMPORTANT IN PRODUCTION)
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .message("Internal Server Error")
                        .errorCode("INTERNAL_ERROR")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(request.getRequestURI())
                        .timestamp(java.time.Instant.now().toString())
                        .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}