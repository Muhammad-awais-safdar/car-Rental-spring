package com.marketplace.pricing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_estimations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String make;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer mileage;

    @Column(name = "condition_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal conditionRating;

    @Column(name = "min_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal minPrice;

    @Column(name = "max_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal maxPrice;

    @Column(name = "avg_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal avgPrice;

    @Column(name = "market_data", columnDefinition = "JSON")
    private String marketData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
