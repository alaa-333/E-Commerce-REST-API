package com.ecommerce.api.dto.response;

import java.time.LocalDateTime;

public record ApiResponse<T> (
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
){
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("operation successful", data);
    }
}
