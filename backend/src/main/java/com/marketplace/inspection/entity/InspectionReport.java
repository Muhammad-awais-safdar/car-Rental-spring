package com.marketplace.inspection.entity;

import com.marketplace.listing.entity.Listing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(name = "inspector_name", nullable = false)
    private String inspectorName;

    @Column(name = "inspection_date", nullable = false)
    private LocalDate inspectionDate;

    @Column(name = "condition_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal conditionRating;

    @Column(columnDefinition = "JSON")
    private String checklist;

    @Column(columnDefinition = "JSON")
    private String photos;

    @Column(name = "issues_found", columnDefinition = "TEXT")
    private String issuesFound;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
