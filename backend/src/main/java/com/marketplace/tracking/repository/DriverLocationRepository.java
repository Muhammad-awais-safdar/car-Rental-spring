package com.marketplace.tracking.repository;

import com.marketplace.tracking.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {

    Optional<DriverLocation> findByDriverId(Long driverId);

    /** All online drivers within a bounding box */
    @Query("""
            SELECT dl FROM DriverLocation dl
            WHERE dl.isOnline = true
            AND dl.latitude  BETWEEN :minLat AND :maxLat
            AND dl.longitude BETWEEN :minLng AND :maxLng
            """)
    List<DriverLocation> findOnlineDriversInBounds(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng
    );

    List<DriverLocation> findByIsOnlineTrue();
}
