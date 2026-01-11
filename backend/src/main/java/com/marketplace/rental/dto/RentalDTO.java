package com.marketplace.rental.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRentalRequest {
        @NotNull(message = "Listing ID is required")
        private Long listingId;

        @NotNull(message = "Daily rate is required")
        @DecimalMin(value = "0.0", message = "Daily rate must be positive")
        private BigDecimal dailyRate;

        @DecimalMin(value = "0.0", message = "Weekly rate must be positive")
        private BigDecimal weeklyRate;

        @DecimalMin(value = "0.0", message = "Monthly rate must be positive")
        private BigDecimal monthlyRate;

        @DecimalMin(value = "0.0", message = "Deposit must be positive")
        private BigDecimal deposit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRentalRequest {
        @DecimalMin(value = "0.0", message = "Daily rate must be positive")
        private BigDecimal dailyRate;

        @DecimalMin(value = "0.0", message = "Weekly rate must be positive")
        private BigDecimal weeklyRate;

        @DecimalMin(value = "0.0", message = "Monthly rate must be positive")
        private BigDecimal monthlyRate;

        @DecimalMin(value = "0.0", message = "Deposit must be positive")
        private BigDecimal deposit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalResponse {
        private Long id;
        private Long listingId;
        private String listingTitle;
        private BigDecimal dailyRate;
        private BigDecimal weeklyRate;
        private BigDecimal monthlyRate;
        private BigDecimal deposit;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityRequest {
        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date must be today or in the future")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date must be today or in the future")
        private LocalDate endDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityResponse {
        private boolean available;
        private String message;
        private BigDecimal estimatedCost;
    }
}
