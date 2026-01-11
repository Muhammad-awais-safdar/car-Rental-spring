package com.marketplace.rental.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DriverDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String licenseNumber;
        private String licenseExpiry;
        private Integer yearsExperience;
        private String bio;
        private String profileImage;
        private BigDecimal hourlyRate;
        private BigDecimal dailyRate;
        private BigDecimal weeklyRate;
        private Boolean isAvailable;
        private BigDecimal rating;
        private Integer totalTrips;
        private String languages;
        private String vehicleTypes;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDriverRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Phone is required")
        private String phone;

        @NotBlank(message = "License number is required")
        private String licenseNumber;

        private String licenseExpiry;
        private Integer yearsExperience;
        private String bio;
        private String profileImage;

        @NotNull(message = "Daily rate is required")
        @DecimalMin(value = "0.0", message = "Daily rate must be positive")
        private BigDecimal dailyRate;

        private BigDecimal hourlyRate;
        private BigDecimal weeklyRate;
        private String languages;
        private String vehicleTypes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDriverRequest {
        private String name;
        private String email;
        private String phone;
        private String bio;
        private String profileImage;
        private BigDecimal hourlyRate;
        private BigDecimal dailyRate;
        private BigDecimal weeklyRate;
        private Boolean isAvailable;
        private String languages;
        private String vehicleTypes;
    }
}
