package com.marketplace.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentRequest {
        @NotNull(message = "Amount is required")
        private BigDecimal amount;

        @NotBlank(message = "Type is required")
        private String type; // SUBSCRIPTION, FEATURED_LISTING, BOOST

        @NotBlank(message = "Payment method is required")
        private String paymentMethod; // CARD, PAYPAL, BANK_TRANSFER

        private String metadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmPaymentRequest {
        @NotBlank(message = "Transaction ID is required")
        private String transactionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private Long userId;
        private BigDecimal amount;
        private String type;
        private String status;
        private String paymentMethod;
        private String transactionId;
        private String metadata;
        private LocalDateTime createdAt;
    }
}
