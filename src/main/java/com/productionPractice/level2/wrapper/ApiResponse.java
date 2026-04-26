package com.productionPractice.level2.wrapper;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse <T>{
    private boolean success;
    private String message;
    private T data;

    public static <T>ApiResponse<T>success(T data,String message)
    {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();

    }

    public static <T>ApiResponse<T>success(String message)
    {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
}
