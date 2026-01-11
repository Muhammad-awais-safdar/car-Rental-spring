package com.marketplace.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PromotionBannerDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBannerRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;
        private String imageUrl;
        private String linkUrl;
        private String buttonText;
        private String position;
        private Boolean isActive;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer displayOrder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BannerResponse {
        private Long id;
        private String title;
        private String description;
        private String imageUrl;
        private String linkUrl;
        private String buttonText;
        private String position;
        private Boolean isActive;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer displayOrder;
        private LocalDateTime createdAt;
    }
}
