package com.marketplace.tracking.service;

import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.rental.entity.Driver;
import com.marketplace.rental.repository.DriverRepository;
import com.marketplace.tracking.dto.LocationDTO;
import com.marketplace.tracking.entity.DriverLocation;
import com.marketplace.tracking.repository.DriverLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    /** ≈5 km radius in decimal degrees */
    private static final BigDecimal NEARBY_DEGREES = new BigDecimal("0.045");

    @Autowired
    private DriverLocationRepository locationRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ─── DRIVER PUSHES LOCATION ───────────────────────────────────────────────

    /**
     * Called when a driver sends a location update via WebSocket.
     * Upserts the driver_locations row and broadcasts to all
     * subscribers of /topic/driver/{driverId}/location.
     */
    public LocationDTO.LocationBroadcast updateDriverLocation(Long userId,
                                                               LocationDTO.LocationUpdate update) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver profile not found for userId: " + userId));

        // Upsert: update existing row or insert new one
        DriverLocation loc = locationRepository.findByDriverId(driver.getId())
                .orElse(DriverLocation.builder().driver(driver).build());

        loc.setLatitude(update.getLatitude());
        loc.setLongitude(update.getLongitude());
        loc.setHeading(update.getHeading() != null ? update.getHeading() : BigDecimal.ZERO);
        loc.setSpeedKmh(update.getSpeedKmh() != null ? update.getSpeedKmh() : BigDecimal.ZERO);
        loc.setAccuracyM(update.getAccuracyM());
        loc.setIsOnline(true);
        loc.setLastSeen(LocalDateTime.now());
        locationRepository.save(loc);

        // Build broadcast payload
        LocationDTO.LocationBroadcast broadcast = LocationDTO.LocationBroadcast.builder()
                .driverId(driver.getId())
                .driverName(driver.getUser().getFirstName() + " " + driver.getUser().getLastName())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .heading(loc.getHeading())
                .speedKmh(loc.getSpeedKmh())
                .isOnline(true)
                .lastSeen(loc.getLastSeen())
                .build();

        // Push to topic — all subscribed passengers/admins receive this
        messagingTemplate.convertAndSend(
                "/topic/driver/" + driver.getId() + "/location",
                broadcast);

        return broadcast;
    }

    /**
     * Driver goes offline — mark as offline and broadcast final state.
     */
    public void setDriverOffline(Long userId) {
        driverRepository.findByUserId(userId).ifPresent(driver ->
                locationRepository.findByDriverId(driver.getId()).ifPresent(loc -> {
                    loc.setIsOnline(false);
                    loc.setLastSeen(LocalDateTime.now());
                    locationRepository.save(loc);

                    messagingTemplate.convertAndSend(
                            "/topic/driver/" + driver.getId() + "/location",
                            LocationDTO.LocationBroadcast.builder()
                                    .driverId(driver.getId())
                                    .driverName(driver.getUser().getFirstName() + " " + driver.getUser().getLastName())
                                    .isOnline(false)
                                    .lastSeen(loc.getLastSeen())
                                    .build());
                })
        );
    }

    // ─── QUERIES (REST endpoints) ─────────────────────────────────────────────

    /**
     * Get the latest location for a specific driver.
     */
    @Transactional(readOnly = true)
    public LocationDTO.OnlineDriverSnapshot getDriverLocation(Long driverId) {
        DriverLocation loc = locationRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No location data found for driverId: " + driverId));
        return toSnapshot(loc);
    }

    /**
     * Get all online drivers within ~5 km of the given coordinates.
     * Used by the passenger's map view.
     */
    @Transactional(readOnly = true)
    public List<LocationDTO.OnlineDriverSnapshot> getNearbyOnlineDrivers(BigDecimal lat, BigDecimal lng) {
        return locationRepository.findOnlineDriversInBounds(
                        lat.subtract(NEARBY_DEGREES), lat.add(NEARBY_DEGREES),
                        lng.subtract(NEARBY_DEGREES), lng.add(NEARBY_DEGREES))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    // ─── CONVERTER ────────────────────────────────────────────────────────────

    private LocationDTO.OnlineDriverSnapshot toSnapshot(DriverLocation loc) {
        Driver d = loc.getDriver();
        return LocationDTO.OnlineDriverSnapshot.builder()
                .driverId(d.getId())
                .driverName(d.getUser().getFirstName() + " " + d.getUser().getLastName())
                .driverVehicleType(d.getVehicleTypes())
                .driverRating(d.getRating())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .heading(loc.getHeading())
                .lastSeen(loc.getLastSeen())
                .build();
    }
}
