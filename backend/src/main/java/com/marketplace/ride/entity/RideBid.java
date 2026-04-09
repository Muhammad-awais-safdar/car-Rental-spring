package com.marketplace.ride.entity;

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

@Entity
@Table(name = "ride_bids",
        uniqueConstraints = @UniqueConstraint(columnNames = {"driver_id", "ride_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RideBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "bid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bidAmount;

    @Column(columnDefinition = "TEXT")
    private String message; // Optional message from driver to passenger

    @Column(name = "estimated_arrival_min")
    private Integer estimatedArrivalMin; // Driver's ETA to pickup in minutes

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";
    // PENDING → ACCEPTED | REJECTED | WITHDRAWN | EXPIRED

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
