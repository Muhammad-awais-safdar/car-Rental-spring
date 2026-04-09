package com.marketplace.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PricingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceEstimationRequest {
        private String make;
        private String model;
        private Integer year;
        private Integer mileage;
        private BigDecimal conditionRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceEstimationResponse {
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private BigDecimal avgPrice;
        private BigDecimal suggestedPrice;
        private String priceRange;
        private Map<String, Object> marketData;
        private List<SimilarVehicle> similarVehicles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimilarVehicle {
        private Long id;
        private String title;
        private BigDecimal price;
        private Integer year;
        private Integer mileage;
        private String location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketTrendResponse {
        private String make;
        private String model;
        private Integer year;
        private BigDecimal averagePrice;
        private BigDecimal priceChange;
        private String trend; // UP, DOWN, STABLE
        private Integer totalListings;
    }
}
