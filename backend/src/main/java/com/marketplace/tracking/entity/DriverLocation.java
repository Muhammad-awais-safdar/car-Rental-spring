package com.marketplace.tracking.entity;

import com.marketplace.rental.entity.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores the LATEST known GPS position for each driver.
 * One row per driver — updated in-place on each location broadcast.
 */
@Entity
@Table(name = "driver_locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false, unique = true)
    private Driver driver;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    /** Compass heading 0-360 degrees */
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal heading = BigDecimal.ZERO;

    @Column(name = "speed_kmh", precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal speedKmh = BigDecimal.ZERO;

    @Column(name = "is_online", nullable = false)
    @Builder.Default
    private Boolean isOnline = true;

    /** GPS accuracy in metres */
    @Column(name = "accuracy_m", precision = 8, scale = 2)
    private BigDecimal accuracyM;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastSeen = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastSeen = LocalDateTime.now();
    }
}
