package com.marketplace.listing.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ListingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateListingRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be positive")
        private BigDecimal price;

        @NotBlank(message = "Make is required")
        private String make;

        @NotBlank(message = "Model is required")
        private String model;

        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Year must be after 1900")
        private Integer year;

        private Integer mileage;

        @NotBlank(message = "Location is required")
        private String location;

        @NotBlank(message = "Listing type is required")
        private String listingType;

        private String fuelType;
        private String transmission;
        private String color;
        private Integer seatingCapacity;
        private String features;
        private List<String> imageUrls;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateListingRequest {
        private String title;
        private String description;
        private BigDecimal price;
        private String make;
        private String model;
        private Integer year;
        private Integer mileage;
        private String location;
        private String fuelType;
        private String transmission;
        private String color;
        private Integer seatingCapacity;
        private String features;
        private List<String> imageUrls;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingResponse {
        private Long id;
        private String slug;
        private String title;
        private String description;
        private BigDecimal price;
        private String make;
        private String model;
        private Integer year;
        private Integer mileage;
        private String location;
        private String listingType;
        private String status;
        private Boolean isFeatured;
        private String fuelType;
        private String transmission;
        private String color;
        private Integer seatingCapacity;
        private String features;
        private OwnerInfo owner;
        private List<String> images;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingSearchRequest {
        private String make;
        private String model;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Integer minYear;
        private Integer maxYear;
        private String location;
        private String listingType;
        private String status;
        private String sortBy;
        private String sortOrder;
        private Integer page;
        private Integer size;
    }
}
