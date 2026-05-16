package com.productionPractice.level2.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // 🔥 extra fields for error/debug context
    private String errorCode;
    private Integer status;
    private String path;
    private String timestamp;
    private Map<String, String> validationErrors;

    // =========================
    // SUCCESS METHODS
    // =========================

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }

    // =========================
    // ERROR METHODS
    // =========================

    public static <T> ApiResponse<T> error(String message, String errorCode, Integer status, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status)
                .path(path)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> validationError(String message,
                                                     Map<String, String> validationErrors,
                                                     String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .status(400)
                .path(path)
                .validationErrors(validationErrors)
                .timestamp(Instant.now().toString())
                .build();
    }
}