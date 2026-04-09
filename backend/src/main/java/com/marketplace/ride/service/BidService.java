package com.marketplace.ride.service;

import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.common.exception.UnauthorizedException;
import com.marketplace.rental.entity.Driver;
import com.marketplace.rental.repository.DriverRepository;
import com.marketplace.ride.dto.RideDTO;
import com.marketplace.ride.entity.Ride;
import com.marketplace.ride.entity.RideBid;
import com.marketplace.ride.repository.RideBidRepository;
import com.marketplace.ride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BidService {

    @Autowired
    private RideBidRepository rideBidRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideService rideService;

    /**
     * Driver places a bid on a ride.
     */
    public RideDTO.BidResponse placeBid(Long rideId, RideDTO.CreateBidRequest request, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (!List.of("SEARCHING", "BIDDING").contains(ride.getStatus())) {
            throw new BadRequestException("Cannot bid on a ride with status: " + ride.getStatus());
        }

        Driver driver = driverRepository.findByUserId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found. Please register as a driver first."));

        if (!driver.getIsAvailable()) {
            throw new BadRequestException("You must be marked as available to place bids");
        }

        // Check if driver is already in an active ride
        List<Ride> activeRides = rideRepository.findActiveRidesByDriver(driverId);
        if (!activeRides.isEmpty()) {
            throw new BadRequestException("You already have an active ride. Complete it before bidding.");
        }

        // Check for duplicate bid
        if (rideBidRepository.existsByRideIdAndDriverIdAndStatus(rideId, driver.getId(), "PENDING")) {
            throw new BadRequestException("You already have a pending bid on this ride. Withdraw it to place a new one.");
        }

        RideBid bid = RideBid.builder()
                .ride(ride)
                .driver(driver)
                .bidAmount(request.getBidAmount())
                .message(request.getMessage())
                .estimatedArrivalMin(request.getEstimatedArrivalMin())
                .status("PENDING")
                .build();

        bid = rideBidRepository.save(bid);

        // Move ride from SEARCHING to BIDDING if it's the first bid
        if ("SEARCHING".equals(ride.getStatus())) {
            ride.setStatus("BIDDING");
            rideRepository.save(ride);
        }

        return rideService.convertBidToResponse(bid);
    }

    /**
     * Driver withdraws their bid.
     */
    public RideDTO.BidResponse withdrawBid(Long bidId, Long driverId) {
        RideBid bid = getBidOrThrow(bidId);

        if (!bid.getDriver().getUser().getId().equals(driverId)) {
            throw new UnauthorizedException("You did not place this bid");
        }

        if (!"PENDING".equals(bid.getStatus())) {
            throw new BadRequestException("Only PENDING bids can be withdrawn. Current status: " + bid.getStatus());
        }

        bid.setStatus("WITHDRAWN");
        bid = rideBidRepository.save(bid);

        return rideService.convertBidToResponse(bid);
    }

    /**
     * Get all bids on a ride (visible to the passenger of that ride).
     */
    @Transactional(readOnly = true)
    public List<RideDTO.BidResponse> getBidsForRide(Long rideId, Long passengerId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        if (!ride.getPassenger().getId().equals(passengerId)) {
            throw new UnauthorizedException("You are not the passenger of this ride");
        }

        return rideBidRepository.findByRideIdOrderByBidAmountAsc(rideId)
                .stream()
                .map(rideService::convertBidToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all bids placed by a driver.
     */
    @Transactional(readOnly = true)
    public List<RideDTO.BidResponse> getMyBids(Long driverId) {
        Driver driver = driverRepository.findByUserId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        return rideBidRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId())
                .stream()
                .map(rideService::convertBidToResponse)
                .collect(Collectors.toList());
    }

    private RideBid getBidOrThrow(Long bidId) {
        return rideBidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + bidId));
    }
}
