package com.marketplace.verification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class VerificationDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmitVerificationRequest {
        private String verificationType; // IDENTITY, PHONE, BUSINESS
        private List<String> documentUrls;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationResponse {
        private Long id;
        private Long userId;
        private String userName;
        private String verificationType;
        private String status;
        private List<String> documentUrls;
        private LocalDateTime submittedAt;
        private LocalDateTime verifiedAt;
        private String verifiedByName;
        private String rejectionReason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationStatusResponse {
        private boolean isVerified;
        private String verificationLevel; // NONE, BASIC, VERIFIED, PREMIUM
        private LocalDateTime verifiedAt;
        private List<VerificationResponse> requests;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproveVerificationRequest {
        private String verificationLevel; // BASIC, VERIFIED, PREMIUM
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RejectVerificationRequest {
        private String rejectionReason;
    }
}
