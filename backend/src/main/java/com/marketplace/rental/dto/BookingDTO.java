package com.marketplace.rental.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBookingRequest {
        @NotNull(message = "Rental ID is required")
        private Long rentalId;

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        private LocalDate endDate;

        // Driver Information (Optional)
        private String driverName;

        @Email(message = "Invalid email format")
        private String driverEmail;

        private String driverPhone;
        private String driverLicense;
        private Boolean needsDriver;

        // Selected Driver ID (Optional - if choosing from driver list)
        private Long driverId;

        // Location Information (Optional)
        private String pickupLocation;
        private String dropoffLocation;

        // Additional Notes
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingResponse {
        private Long id;
        private Long rentalId;
        private String listingTitle;
        private String listingImage;
        private Long userId;
        private String userName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalAmount;
        private String status;

        // Driver Information
        private String driverName;
        private String driverEmail;
        private String driverPhone;
        private String driverLicense;
        private Boolean needsDriver;

        // Assigned Driver (from driver list)
        private Long assignedDriverId;
        private String assignedDriverName;
        private BigDecimal driverRate;

        // Location Information
        private String pickupLocation;
        private String dropoffLocation;

        // Additional Notes
        private String notes;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingListResponse {
        private Long id;
        private String listingTitle;
        private String listingImage;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
    }
}
