package com.marketplace.ride.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.common.exception.UnauthorizedException;
import com.marketplace.rental.entity.Driver;
import com.marketplace.ride.dto.RideDTO;
import com.marketplace.ride.entity.Ride;
import com.marketplace.ride.entity.RideBid;
import com.marketplace.ride.repository.RideBidRepository;
import com.marketplace.ride.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideService {

    // ≈ 1 degree of latitude/longitude ≈ 111 km, so 5 km radius ≈ 0.045 degrees
    private static final BigDecimal NEARBY_RADIUS_DEGREES = new BigDecimal("0.045");
    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("15.00"); // 15%

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideBidRepository rideBidRepository;

    @Autowired
    private UserRepository userRepository;

    // ─── PASSENGER ACTIONS ───────────────────────────────────

    /**
     * Passenger creates a new ride request.
     */
    public RideDTO.RideResponse createRide(RideDTO.CreateRideRequest request, Long passengerId) {
        User passenger = userRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if passenger already has an active ride
        List<Ride> activeRides = rideRepository.findActiveRidesByPassenger(passengerId);
        if (!activeRides.isEmpty()) {
            throw new BadRequestException("You already have an active ride. Complete or cancel it first.");
        }

        Ride ride = Ride.builder()
                .passenger(passenger)
                .pickupAddress(request.getPickupAddress())
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .dropoffAddress(request.getDropoffAddress())
                .dropoffLat(request.getDropoffLat())
                .dropoffLng(request.getDropoffLng())
                .offeredPrice(request.getOfferedPrice())
                .vehicleType(request.getVehicleType() != null ? request.getVehicleType() : "ANY")
                .notes(request.getNotes())
                .estimatedDistanceKm(request.getEstimatedDistanceKm())
                .estimatedDurationMin(request.getEstimatedDurationMin())
                .status("SEARCHING")
                .build();

        ride = rideRepository.save(ride);
        return convertToResponse(ride);
    }

    /**
     * Passenger accepts a specific driver's bid.
     */
    public RideDTO.RideResponse acceptBid(Long rideId, Long bidId, Long passengerId) {
        Ride ride = getRideOrThrow(rideId);

        // Only the passenger can accept bids
        if (!ride.getPassenger().getId().equals(passengerId)) {
            throw new UnauthorizedException("You are not the passenger of this ride");
        }

        if (!List.of("SEARCHING", "BIDDING").contains(ride.getStatus())) {
            throw new BadRequestException("Ride is not in a biddable state. Current status: " + ride.getStatus());
        }

        RideBid bid = rideBidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        if (!bid.getRide().getId().equals(rideId)) {
            throw new BadRequestException("This bid does not belong to the specified ride");
        }

        if (!"PENDING".equals(bid.getStatus())) {
            throw new BadRequestException("This bid is no longer available. Status: " + bid.getStatus());
        }

        // Accept the chosen bid
        bid.setStatus("ACCEPTED");
        rideBidRepository.save(bid);

        // Reject all other pending bids on this ride
        List<RideBid> otherBids = rideBidRepository.findByRideIdOrderByBidAmountAsc(rideId);
        otherBids.stream()
                .filter(b -> !b.getId().equals(bidId) && "PENDING".equals(b.getStatus()))
                .forEach(b -> {
                    b.setStatus("REJECTED");
                    rideBidRepository.save(b);
                });

        // Update ride
        ride.setAcceptedBid(bid);
        ride.setAssignedDriver(bid.getDriver());
        ride.setFinalPrice(bid.getBidAmount());
        ride.setStatus("ACCEPTED");
        ride.setAcceptedAt(LocalDateTime.now());
        ride = rideRepository.save(ride);

        return convertToResponse(ride);
    }

    /**
     * Passenger cancels the ride (before it starts).
     */
    public RideDTO.RideResponse cancelRideByPassenger(Long rideId, String reason, Long passengerId) {
        Ride ride = getRideOrThrow(rideId);

        if (!ride.getPassenger().getId().equals(passengerId)) {
            throw new UnauthorizedException("You are not the passenger of this ride");
        }

        if (List.of("IN_PROGRESS", "COMPLETED", "CANCELLED").contains(ride.getStatus())) {
            throw new BadRequestException("Cannot cancel ride in current state: " + ride.getStatus());
        }

        ride.setStatus("CANCELLED");
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(reason);
        ride.setCancelledBy("PASSENGER");
        ride = rideRepository.save(ride);

        return convertToResponse(ride);
    }

    // ─── DRIVER ACTIONS ──────────────────────────────────────

    /**
     * Driver marks themselves as arrived at pickup.
     */
    public RideDTO.RideResponse markDriverArrived(Long rideId, Long driverId) {
        Ride ride = getRideOrThrow(rideId);
        validateDriverAssigned(ride, driverId);

        if (!"ACCEPTED".equals(ride.getStatus())) {
            throw new BadRequestException("Can only mark arrived when ride is ACCEPTED. Current: " + ride.getStatus());
        }

        ride.setStatus("DRIVER_ARRIVING");
        ride.setDriverArrivedAt(LocalDateTime.now());
        ride = rideRepository.save(ride);

        return convertToResponse(ride);
    }

    /**
     * Driver starts the ride (passenger has boarded).
     */
    public RideDTO.RideResponse startRide(Long rideId, Long driverId) {
        Ride ride = getRideOrThrow(rideId);
        validateDriverAssigned(ride, driverId);

        if (!List.of("ACCEPTED", "DRIVER_ARRIVING").contains(ride.getStatus())) {
            throw new BadRequestException("Cannot start ride from current state: " + ride.getStatus());
        }

        ride.setStatus("IN_PROGRESS");
        ride.setStartedAt(LocalDateTime.now());
        ride = rideRepository.save(ride);

        return convertToResponse(ride);
    }

    /**
     * Driver completes the ride and triggers commission calculation.
     */
    public RideDTO.RideResponse completeRide(Long rideId, Long driverId) {
        Ride ride = getRideOrThrow(rideId);
        validateDriverAssigned(ride, driverId);

        if (!"IN_PROGRESS".equals(ride.getStatus())) {
            throw new BadRequestException("Can only complete a ride that is IN_PROGRESS. Current: " + ride.getStatus());
        }

        // Apply default commission (Phase 3+ will use CommissionService)
        BigDecimal commissionRate = DEFAULT_COMMISSION_RATE;
        BigDecimal finalPrice = ride.getFinalPrice();
        BigDecimal commissionAmount = finalPrice.multiply(commissionRate)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal driverEarning = finalPrice.subtract(commissionAmount);

        ride.setCommissionRate(commissionRate);
        ride.setCommissionAmount(commissionAmount);
        ride.setDriverEarning(driverEarning);
        ride.setStatus("COMPLETED");
        ride.setCompletedAt(LocalDateTime.now());
        ride = rideRepository.save(ride);

        // TODO Phase 4: Credit driver wallet with driverEarning

        return convertToResponse(ride);
    }

    /**
     * Driver cancels the ride.
     */
    public RideDTO.RideResponse cancelRideByDriver(Long rideId, String reason, Long driverId) {
        Ride ride = getRideOrThrow(rideId);
        validateDriverAssigned(ride, driverId);

        if (List.of("IN_PROGRESS", "COMPLETED", "CANCELLED").contains(ride.getStatus())) {
            throw new BadRequestException("Cannot cancel ride in current state: " + ride.getStatus());
        }

        ride.setStatus("CANCELLED");
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(reason);
        ride.setCancelledBy("DRIVER");
        ride = rideRepository.save(ride);

        return convertToResponse(ride);
    }

    // ─── QUERIES ─────────────────────────────────────────────

    public RideDTO.RideResponse getRideById(Long rideId, Long userId) {
        Ride ride = getRideOrThrow(rideId);
        // Allow passenger, assigned driver, or admin to view
        return convertToResponse(ride);
    }

    public Page<RideDTO.RideListResponse> getMyRidesAsPassenger(Long passengerId, Pageable pageable) {
        return rideRepository.findByPassengerId(passengerId, pageable)
                .map(this::convertToListResponse);
    }

    public Page<RideDTO.RideListResponse> getMyRidesAsDriver(Long driverId, Pageable pageable) {
        return rideRepository.findByAssignedDriverId(driverId, pageable)
                .map(this::convertToListResponse);
    }

    /**
     * Returns rides within ~5 km radius that are in SEARCHING or BIDDING status.
     */
    public List<RideDTO.NearbyRideResponse> getNearbyRides(BigDecimal lat, BigDecimal lng) {
        BigDecimal minLat = lat.subtract(NEARBY_RADIUS_DEGREES);
        BigDecimal maxLat = lat.add(NEARBY_RADIUS_DEGREES);
        BigDecimal minLng = lng.subtract(NEARBY_RADIUS_DEGREES);
        BigDecimal maxLng = lng.add(NEARBY_RADIUS_DEGREES);

        return rideRepository.findNearbySearchingRides(minLat, maxLat, minLng, maxLng)
                .stream()
                .map(this::convertToNearbyResponse)
                .collect(Collectors.toList());
    }

    // ─── HELPERS ─────────────────────────────────────────────

    private Ride getRideOrThrow(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
    }

    private void validateDriverAssigned(Ride ride, Long driverId) {
        if (ride.getAssignedDriver() == null || !ride.getAssignedDriver().getId().equals(driverId)) {
            throw new UnauthorizedException("You are not the assigned driver for this ride");
        }
    }

    // ─── CONVERTERS ──────────────────────────────────────────

    public RideDTO.RideResponse convertToResponse(Ride ride) {
        List<RideDTO.BidResponse> bidResponses = ride.getBids() != null
                ? ride.getBids().stream().map(this::convertBidToResponse).collect(Collectors.toList())
                : List.of();

        RideDTO.RideResponse.RideResponseBuilder builder = RideDTO.RideResponse.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .passengerId(ride.getPassenger().getId())
                .passengerName(ride.getPassenger().getFirstName() + " " + ride.getPassenger().getLastName())
                .pickupAddress(ride.getPickupAddress())
                .pickupLat(ride.getPickupLat())
                .pickupLng(ride.getPickupLng())
                .dropoffAddress(ride.getDropoffAddress())
                .dropoffLat(ride.getDropoffLat())
                .dropoffLng(ride.getDropoffLng())
                .offeredPrice(ride.getOfferedPrice())
                .finalPrice(ride.getFinalPrice())
                .commissionAmount(ride.getCommissionAmount())
                .commissionRate(ride.getCommissionRate())
                .driverEarning(ride.getDriverEarning())
                .vehicleType(ride.getVehicleType())
                .notes(ride.getNotes())
                .estimatedDistanceKm(ride.getEstimatedDistanceKm())
                .estimatedDurationMin(ride.getEstimatedDurationMin())
                .bids(bidResponses)
                .bidCount((long) bidResponses.size())
                .acceptedAt(ride.getAcceptedAt())
                .driverArrivedAt(ride.getDriverArrivedAt())
                .startedAt(ride.getStartedAt())
                .completedAt(ride.getCompletedAt())
                .cancelledAt(ride.getCancelledAt())
                .cancellationReason(ride.getCancellationReason())
                .cancelledBy(ride.getCancelledBy())
                .createdAt(ride.getCreatedAt())
                .updatedAt(ride.getUpdatedAt());

        if (ride.getAssignedDriver() != null) {
            Driver d = ride.getAssignedDriver();
            builder.assignedDriverId(d.getId())
                    .assignedDriverName(d.getUser().getFirstName() + " " + d.getUser().getLastName())
                    .assignedDriverPhone(d.getUser().getPhone())
                    .assignedDriverRating(d.getRating())
                    .assignedDriverVehicle(d.getVehicleTypes());
        }

        return builder.build();
    }

    private RideDTO.RideListResponse convertToListResponse(Ride ride) {
        long bidCount = rideBidRepository.countByRideIdAndStatus(ride.getId(), "PENDING");
        return RideDTO.RideListResponse.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .pickupAddress(ride.getPickupAddress())
                .dropoffAddress(ride.getDropoffAddress())
                .offeredPrice(ride.getOfferedPrice())
                .finalPrice(ride.getFinalPrice())
                .vehicleType(ride.getVehicleType())
                .bidCount(bidCount)
                .assignedDriverName(ride.getAssignedDriver() != null
                        ? ride.getAssignedDriver().getUser().getFirstName() + " " + ride.getAssignedDriver().getUser().getLastName()
                        : null)
                .createdAt(ride.getCreatedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }

    private RideDTO.NearbyRideResponse convertToNearbyResponse(Ride ride) {
        long bidCount = rideBidRepository.countByRideIdAndStatus(ride.getId(), "PENDING");
        return RideDTO.NearbyRideResponse.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .pickupAddress(ride.getPickupAddress())
                .pickupLat(ride.getPickupLat())
                .pickupLng(ride.getPickupLng())
                .dropoffAddress(ride.getDropoffAddress())
                .offeredPrice(ride.getOfferedPrice())
                .vehicleType(ride.getVehicleType())
                .notes(ride.getNotes())
                .estimatedDistanceKm(ride.getEstimatedDistanceKm())
                .estimatedDurationMin(ride.getEstimatedDurationMin())
                .bidCount(bidCount)
                .createdAt(ride.getCreatedAt())
                .build();
    }

    public RideDTO.BidResponse convertBidToResponse(RideBid bid) {
        Driver driver = bid.getDriver();
        return RideDTO.BidResponse.builder()
                .id(bid.getId())
                .rideId(bid.getRide().getId())
                .status(bid.getStatus())
                .driverId(driver.getId())
                .driverName(driver.getUser().getFirstName() + " " + driver.getUser().getLastName())
                .driverPhone(driver.getUser().getPhone())
                .driverRating(driver.getRating())
                .driverTotalTrips(driver.getTotalTrips())
                .driverProfileImage(driver.getProfileImage())
                .driverVehicleTypes(driver.getVehicleTypes())
                .bidAmount(bid.getBidAmount())
                .message(bid.getMessage())
                .estimatedArrivalMin(bid.getEstimatedArrivalMin())
                .createdAt(bid.getCreatedAt())
                .build();
    }
}
