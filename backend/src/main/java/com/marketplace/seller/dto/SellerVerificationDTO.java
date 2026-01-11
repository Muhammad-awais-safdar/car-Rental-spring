package com.marketplace.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SellerVerificationDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationRequest {
        @NotBlank(message = "Document type is required")
        private String documentType;

        @NotBlank(message = "Document URL is required")
        private String documentUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationResponse {
        private Long id;
        private Long userId;
        private String userName;
        private String documentType;
        private String documentUrl;
        private String status;
        private Long reviewedBy;
        private String reviewedByName;
        private LocalDateTime reviewedAt;
        private String rejectionReason;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewRequest {
        private String rejectionReason;
    }
}
