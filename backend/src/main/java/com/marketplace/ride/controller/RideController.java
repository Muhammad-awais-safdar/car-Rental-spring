package com.marketplace.ride.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.ride.dto.RideDTO;
import com.marketplace.ride.service.RideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    // ─── PASSENGER ENDPOINTS ─────────────────────────────────

    /**
     * POST /api/rides
     * Passenger creates a new ride request.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> createRide(
            @Valid @RequestBody RideDTO.CreateRideRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.createRide(request, userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_RIDE_CREATED, response),
                HttpStatus.CREATED);
    }

    /**
     * PUT /api/rides/{id}/accept-bid/{bidId}
     * Passenger accepts a specific driver's bid.
     */
    @PutMapping("/{id}/accept-bid/{bidId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> acceptBid(
            @PathVariable Long id,
            @PathVariable Long bidId,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.acceptBid(id, bidId, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_BID_ACCEPTED, response));
    }

    /**
     * PUT /api/rides/{id}/cancel
     * Passenger cancels their ride.
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> cancelRideByPassenger(
            @PathVariable Long id,
            @RequestBody(required = false) RideDTO.CancelRideRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        String reason = request != null ? request.getReason() : null;
        RideDTO.RideResponse response = rideService.cancelRideByPassenger(id, reason, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RIDE_CANCELLED, response));
    }

    // ─── DRIVER ENDPOINTS ────────────────────────────────────

    /**
     * PUT /api/rides/{id}/driver-arrived
     * Driver marks themselves as arrived at pickup.
     */
    @PutMapping("/{id}/driver-arrived")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> markDriverArrived(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.markDriverArrived(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Driver has arrived at pickup", response));
    }

    /**
     * PUT /api/rides/{id}/start
     * Driver starts the ride.
     */
    @PutMapping("/{id}/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> startRide(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.startRide(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RIDE_STARTED, response));
    }

    /**
     * PUT /api/rides/{id}/complete
     * Driver completes the ride.
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> completeRide(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.completeRide(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RIDE_COMPLETED, response));
    }

    /**
     * PUT /api/rides/{id}/driver-cancel
     * Driver cancels the ride.
     */
    @PutMapping("/{id}/driver-cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> cancelRideByDriver(
            @PathVariable Long id,
            @RequestBody(required = false) RideDTO.CancelRideRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        String reason = request != null ? request.getReason() : null;
        RideDTO.RideResponse response = rideService.cancelRideByDriver(id, reason, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RIDE_CANCELLED, response));
    }

    // ─── SHARED ENDPOINTS ────────────────────────────────────

    /**
     * GET /api/rides/{id}
     * Get ride details.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RideDTO.RideResponse>> getRideById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RideDTO.RideResponse response = rideService.getRideById(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }

    /**
     * GET /api/rides/my/passenger
     * Passenger's ride history.
     */
    @GetMapping("/my/passenger")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<RideDTO.RideListResponse>>> getMyRidesAsPassenger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RideDTO.RideListResponse> response = rideService.getMyRidesAsPassenger(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }

    /**
     * GET /api/rides/my/driver
     * Driver's ride history.
     */
    @GetMapping("/my/driver")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<RideDTO.RideListResponse>>> getMyRidesAsDriver(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RideDTO.RideListResponse> response = rideService.getMyRidesAsDriver(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }

    /**
     * GET /api/rides/nearby?lat=&lng=
     * Drivers browse nearby ride requests.
     */
    @GetMapping("/nearby")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RideDTO.NearbyRideResponse>>> getNearbyRides(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng) {

        List<RideDTO.NearbyRideResponse> response = rideService.getNearbyRides(lat, lng);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response));
    }
}
