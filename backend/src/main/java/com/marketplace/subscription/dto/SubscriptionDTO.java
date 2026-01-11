package com.marketplace.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SubscriptionDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscribeRequest {
        @NotBlank(message = "Plan is required")
        private String plan; // FREE, BASIC, PRO, PREMIUM
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionResponse {
        private Long id;
        private Long userId;
        private String plan;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
        private Boolean autoRenew;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanDetails {
        private String name;
        private String displayName;
        private Double price;
        private Integer listingLimit;
        private String description;
        private Boolean featured;
        private Boolean prioritySupport;
    }
}
