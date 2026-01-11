package com.marketplace.listing.repository;

import com.marketplace.listing.entity.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

        Optional<Listing> findBySlug(String slug);

        Page<Listing> findByStatus(String status, Pageable pageable);

        Page<Listing> findByOwnerId(Long ownerId, Pageable pageable);

        @Query("SELECT l FROM Listing l WHERE " +
                        "(:make IS NULL OR l.make = :make) AND " +
                        "(:model IS NULL OR l.model = :model) AND " +
                        "(:minYear IS NULL OR l.year >= :minYear) AND " +
                        "(:maxYear IS NULL OR l.year <= :maxYear) AND " +
                        "(:minPrice IS NULL OR l.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR l.price <= :maxPrice) AND " +
                        "(:location IS NULL OR l.location LIKE %:location%) AND " +
                        "(:listingType IS NULL OR l.listingType = :listingType) AND " +
                        "(:status IS NULL OR l.status = :status)")
        Page<Listing> searchListings(
                        @Param("make") String make,
                        @Param("model") String model,
                        @Param("minYear") Integer minYear,
                        @Param("maxYear") Integer maxYear,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("location") String location,
                        @Param("listingType") String listingType,
                        @Param("status") String status,
                        Pageable pageable);

        // Analytics queries
        long countByCreatedAtAfter(LocalDateTime date);
}
