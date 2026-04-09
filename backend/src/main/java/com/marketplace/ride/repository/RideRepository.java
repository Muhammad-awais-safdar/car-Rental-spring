package com.marketplace.ride.repository;

import com.marketplace.ride.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Get all rides for a passenger
    Page<Ride> findByPassengerId(Long passengerId, Pageable pageable);

    // Get all rides assigned to a driver
    Page<Ride> findByAssignedDriverId(Long driverId, Pageable pageable);

    // Get rides by status
    Page<Ride> findByStatus(String status, Pageable pageable);

    // Get passenger's rides by status
    Page<Ride> findByPassengerIdAndStatus(Long passengerId, String status, Pageable pageable);

    // Get active rides for a passenger (not completed/cancelled)
    @Query("SELECT r FROM Ride r WHERE r.passenger.id = :passengerId AND r.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Ride> findActiveRidesByPassenger(@Param("passengerId") Long passengerId);

    // Get active ride for a driver (only one at a time)
    @Query("SELECT r FROM Ride r WHERE r.assignedDriver.id = :driverId AND r.status IN ('ACCEPTED', 'DRIVER_ARRIVING', 'IN_PROGRESS')")
    List<Ride> findActiveRidesByDriver(@Param("driverId") Long driverId);

    // Find nearby rides that are searching/bidding (within a bounding box for performance)
    @Query("""
            SELECT r FROM Ride r
            WHERE r.status IN ('SEARCHING', 'BIDDING')
            AND r.pickupLat BETWEEN :minLat AND :maxLat
            AND r.pickupLng BETWEEN :minLng AND :maxLng
            ORDER BY r.createdAt DESC
            """)
    List<Ride> findNearbySearchingRides(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng
    );
}
