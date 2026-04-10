package com.marketplace.tracking.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.tracking.dto.LocationDTO;
import com.marketplace.tracking.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Dual-purpose controller:
 *  - @MessageMapping  — handles STOMP messages from the WebSocket client
 *  - @RestController  — HTTP REST endpoints for initial map load
 */
@RestController
@RequestMapping("/api/tracking")
public class DriverLocationController {

    @Autowired
    private LocationService locationService;

    // ─── WebSocket STOMP handlers ─────────────────────────────────────────────

    /**
     * Driver sends: SEND /app/driver/location  { latitude, longitude, heading, speedKmh }
     * Server:       broadcasts to /topic/driver/{driverId}/location
     *
     * The Principal is set from the JWT extracted during CONNECT (WebSocketConfig).
     */
    @MessageMapping("/driver/location")
    public void handleLocationUpdate(@Payload LocationDTO.LocationUpdate update,
                                     Principal principal) {
        if (principal == null) return;
        Long userId = Long.parseLong(principal.getName());
        locationService.updateDriverLocation(userId, update);
    }

    /**
     * Driver sends: SEND /app/driver/offline  {}
     * Marks the driver as offline and broadcasts the state change.
     */
    @MessageMapping("/driver/offline")
    public void handleDriverOffline(Principal principal) {
        if (principal == null) return;
        Long userId = Long.parseLong(principal.getName());
        locationService.setDriverOffline(userId);
    }

    // ─── REST endpoints ───────────────────────────────────────────────────────

    /**
     * GET /api/tracking/drivers/online?lat=&lng=
     * Returns all online drivers within ~5 km. Used for initial map render.
     */
    @GetMapping("/drivers/online")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LocationDTO.OnlineDriverSnapshot>>> getNearbyDrivers(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng) {

        List<LocationDTO.OnlineDriverSnapshot> drivers =
                locationService.getNearbyOnlineDrivers(lat, lng);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, drivers));
    }

    /**
     * GET /api/tracking/drivers/{driverId}/location
     * Passenger polls the driver's latest known position (HTTP fallback).
     */
    @GetMapping("/drivers/{driverId}/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LocationDTO.OnlineDriverSnapshot>> getDriverLocation(
            @PathVariable Long driverId) {

        LocationDTO.OnlineDriverSnapshot snapshot =
                locationService.getDriverLocation(driverId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, snapshot));
    }

    /**
     * POST /api/tracking/driver/location  (HTTP fallback for driver location push)
     * Useful for clients that don't support WebSocket (e.g. some IoT devices).
     */
    @PostMapping("/driver/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LocationDTO.LocationBroadcast>> updateLocationHttp(
            @RequestBody LocationDTO.LocationUpdate update,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        LocationDTO.LocationBroadcast broadcast =
                locationService.updateDriverLocation(userId, update);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Location updated", broadcast));
    }
}
