package com.marketplace.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MakeRequest {
        @NotBlank(message = "Make name is required")
        private String name;
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MakeResponse {
        private Long id;
        private String name;
        private String logoUrl;
        private Integer modelCount;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelRequest {
        @NotBlank(message = "Model name is required")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelResponse {
        private Long id;
        private Long makeId;
        private String makeName;
        private String name;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MakeWithModelsResponse {
        private Long id;
        private String name;
        private String logoUrl;
        private List<ModelResponse> models;
    }
}
