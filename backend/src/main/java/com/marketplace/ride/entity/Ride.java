package com.marketplace.ride.entity;

import com.marketplace.auth.entity.User;
import com.marketplace.rental.entity.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Passenger who requested the ride
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    // Pickup location
    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "pickup_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal pickupLat;

    @Column(name = "pickup_lng", nullable = false, precision = 11, scale = 8)
    private BigDecimal pickupLng;

    // Dropoff location
    @Column(name = "dropoff_address", nullable = false)
    private String dropoffAddress;

    @Column(name = "dropoff_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal dropoffLat;

    @Column(name = "dropoff_lng", nullable = false, precision = 11, scale = 8)
    private BigDecimal dropoffLng;

    // Passenger's offered price (null = open for driver bids)
    @Column(name = "offered_price", precision = 10, scale = 2)
    private BigDecimal offeredPrice;

    // Ride preferences
    @Column(name = "vehicle_type", length = 50)
    @Builder.Default
    private String vehicleType = "ANY"; // CAR, BIKE, VAN, ANY

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "estimated_distance_km", precision = 8, scale = 2)
    private BigDecimal estimatedDistanceKm;

    @Column(name = "estimated_duration_min")
    private Integer estimatedDurationMin;

    // Accepted bid and driver (populated after passenger accepts a bid)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_bid_id")
    private RideBid acceptedBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    // Commission details (populated on completion)
    @Column(name = "commission_amount", precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "driver_earning", precision = 10, scale = 2)
    private BigDecimal driverEarning;

    // Ride lifecycle status
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "SEARCHING";
    // SEARCHING → BIDDING → ACCEPTED → DRIVER_ARRIVING → IN_PROGRESS → COMPLETED | CANCELLED

    // Lifecycle timestamps
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "driver_arrived_at")
    private LocalDateTime driverArrivedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy; // PASSENGER, DRIVER, SYSTEM

    // All bids on this ride
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RideBid> bids = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
