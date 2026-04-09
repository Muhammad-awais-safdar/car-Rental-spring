package com.marketplace.inspection.repository;

import com.marketplace.inspection.entity.InspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionReportRepository extends JpaRepository<InspectionReport, Long> {

    List<InspectionReport> findByListingIdOrderByInspectionDateDesc(Long listingId);

    @Query("SELECT i FROM InspectionReport i WHERE i.listing.id = :listingId AND i.validUntil >= :date ORDER BY i.inspectionDate DESC")
    Optional<InspectionReport> findValidInspectionByListingId(
            @Param("listingId") Long listingId,
            @Param("date") LocalDate date);
}
