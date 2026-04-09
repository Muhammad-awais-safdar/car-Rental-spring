package com.marketplace.ride.repository;

import com.marketplace.ride.entity.RideBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideBidRepository extends JpaRepository<RideBid, Long> {

    // All bids on a specific ride
    List<RideBid> findByRideIdOrderByBidAmountAsc(Long rideId);

    // A specific driver's bid on a specific ride
    Optional<RideBid> findByRideIdAndDriverId(Long rideId, Long driverId);

    // All bids from a driver
    List<RideBid> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    // Check if driver already has a pending bid on a ride
    boolean existsByRideIdAndDriverIdAndStatus(Long rideId, Long driverId, String status);

    // Count pending bids on a ride
    long countByRideIdAndStatus(Long rideId, String status);
}
