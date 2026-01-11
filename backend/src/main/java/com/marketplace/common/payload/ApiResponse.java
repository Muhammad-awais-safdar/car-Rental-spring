package com.marketplace.common.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String statusMessage;
    private T data;

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .statusMessage(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(int statusCode, String message) {
        return success(statusCode, message, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .statusMessage(message)
                .data(null)
                .build();
    }
}
