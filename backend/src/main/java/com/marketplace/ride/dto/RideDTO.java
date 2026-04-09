package com.marketplace.ride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RideDTO {

    // ─── REQUEST DTOs ─────────────────────────────────────────

    /** Passenger creates a new ride request */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRideRequest {

        @NotBlank(message = "Pickup address is required")
        private String pickupAddress;

        @NotNull(message = "Pickup latitude is required")
        private BigDecimal pickupLat;

        @NotNull(message = "Pickup longitude is required")
        private BigDecimal pickupLng;

        @NotBlank(message = "Dropoff address is required")
        private String dropoffAddress;

        @NotNull(message = "Dropoff latitude is required")
        private BigDecimal dropoffLat;

        @NotNull(message = "Dropoff longitude is required")
        private BigDecimal dropoffLng;

        // Optional: passenger can offer a price upfront
        @DecimalMin(value = "0.0", inclusive = false, message = "Offered price must be positive")
        private BigDecimal offeredPrice;

        private String vehicleType; // CAR, BIKE, VAN, ANY
        private String notes;
        private BigDecimal estimatedDistanceKm;
        private Integer estimatedDurationMin;
    }

    /** Passenger cancels a ride */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRideRequest {
        private String reason;
    }

    // ─── RESPONSE DTOs ────────────────────────────────────────

    /** Full ride details */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RideResponse {
        private Long id;
        private String status;

        // Passenger info
        private Long passengerId;
        private String passengerName;

        // Location
        private String pickupAddress;
        private BigDecimal pickupLat;
        private BigDecimal pickupLng;
        private String dropoffAddress;
        private BigDecimal dropoffLat;
        private BigDecimal dropoffLng;

        // Pricing
        private BigDecimal offeredPrice;
        private BigDecimal finalPrice;
        private BigDecimal commissionAmount;
        private BigDecimal commissionRate;
        private BigDecimal driverEarning;

        // Ride detail
        private String vehicleType;
        private String notes;
        private BigDecimal estimatedDistanceKm;
        private Integer estimatedDurationMin;

        // Driver details (set after acceptance)
        private Long assignedDriverId;
        private String assignedDriverName;
        private String assignedDriverPhone;
        private BigDecimal assignedDriverRating;
        private String assignedDriverVehicle;

        // Bids on this ride
        private List<BidResponse> bids;
        private Long bidCount;

        // Lifecycle times
        private LocalDateTime acceptedAt;
        private LocalDateTime driverArrivedAt;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private LocalDateTime cancelledAt;
        private String cancellationReason;
        private String cancelledBy;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /** Compact ride listing for lists/history */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RideListResponse {
        private Long id;
        private String status;
        private String pickupAddress;
        private String dropoffAddress;
        private BigDecimal offeredPrice;
        private BigDecimal finalPrice;
        private String vehicleType;
        private Long bidCount;
        private String assignedDriverName;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }

    /** Nearby ride summary (for drivers browsing available rides) */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyRideResponse {
        private Long id;
        private String status;
        private String pickupAddress;
        private String dropoffAddress;
        private BigDecimal pickupLat;
        private BigDecimal pickupLng;
        private BigDecimal offeredPrice;
        private String vehicleType;
        private String notes;
        private BigDecimal estimatedDistanceKm;
        private Integer estimatedDurationMin;
        private Long bidCount;
        private LocalDateTime createdAt;
    }

    // ─── BID DTOs ────────────────────────────────────────────

    /** Driver places a bid on a ride */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBidRequest {

        @NotNull(message = "Bid amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Bid amount must be positive")
        private BigDecimal bidAmount;

        private String message;
        private Integer estimatedArrivalMin;
    }

    /** Bid details */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidResponse {
        private Long id;
        private Long rideId;
        private String status;

        // Driver summary
        private Long driverId;
        private String driverName;
        private String driverPhone;
        private BigDecimal driverRating;
        private Integer driverTotalTrips;
        private String driverProfileImage;
        private String driverVehicleTypes;

        // Bid details
        private BigDecimal bidAmount;
        private String message;
        private Integer estimatedArrivalMin;

        private LocalDateTime createdAt;
    }

    // ─── STATUS UPDATE PAYLOADS ───────────────────────────────

    /** Driver marks themselves as arrived at pickup */
    @Data
    @NoArgsConstructor
    public static class DriverArrivedRequest {}

    /** Driver starts the ride */
    @Data
    @NoArgsConstructor
    public static class StartRideRequest {}

    /** Driver or passenger completes the ride */
    @Data
    @NoArgsConstructor
    public static class CompleteRideRequest {}
}
