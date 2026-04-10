package com.marketplace.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LocationDTO {

    // ─── INBOUND (driver → server via WebSocket) ──────────────────────────────

    /** Payload the driver app sends to publish their location. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationUpdate {
        @NotNull private BigDecimal latitude;
        @NotNull private BigDecimal longitude;
        private BigDecimal heading;     // optional compass heading 0-360
        private BigDecimal speedKmh;    // optional
        private BigDecimal accuracyM;   // GPS accuracy in metres
    }

    // ─── OUTBOUND (server → subscribers via WebSocket topic) ──────────────────

    /** Broadcast payload pushed to all subscribers of /topic/driver/{driverId}/location */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationBroadcast {
        private Long   driverId;
        private String driverName;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private BigDecimal heading;
        private BigDecimal speedKmh;
        private Boolean    isOnline;
        private LocalDateTime lastSeen;
    }

    // ─── REST response (GET /api/tracking/drivers/online) ────────────────────

    /** Snapshot of a driver shown on the passenger's map */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnlineDriverSnapshot {
        private Long   driverId;
        private String driverName;
        private String driverVehicleType;
        private BigDecimal driverRating;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private BigDecimal heading;
        private LocalDateTime lastSeen;
    }
}
