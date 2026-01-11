package com.marketplace.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCouponRequest {
        @NotBlank(message = "Code is required")
        private String code;

        @NotBlank(message = "Discount type is required")
        private String discountType;

        @NotNull(message = "Discount value is required")
        private BigDecimal discountValue;

        private BigDecimal minAmount;
        private Integer maxUses;
        private LocalDateTime expiresAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponResponse {
        private Long id;
        private String code;
        private String discountType;
        private BigDecimal discountValue;
        private BigDecimal minAmount;
        private Integer maxUses;
        private Integer currentUses;
        private LocalDateTime expiresAt;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateCouponResponse {
        private Boolean valid;
        private String message;
        private BigDecimal discountAmount;
        private CouponResponse coupon;
    }
}
