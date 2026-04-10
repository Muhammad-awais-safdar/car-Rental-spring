-- V32: Phase 2 - Driver Live Location Tracking
-- Stores the latest GPS position of each online driver
-- This table is updated in real-time as drivers broadcast their location

CREATE TABLE IF NOT EXISTS driver_locations (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id   BIGINT NOT NULL UNIQUE,

    -- GPS coordinates
    latitude    DECIMAL(10, 8) NOT NULL,
    longitude   DECIMAL(11, 8) NOT NULL,

    -- Heading (0-360 degrees) and speed for map animation
    heading     DECIMAL(5, 2) DEFAULT 0.00,
    speed_kmh   DECIMAL(6, 2) DEFAULT 0.00,

    -- Status flags
    is_online   BOOLEAN NOT NULL DEFAULT TRUE,
    -- Accuracy of the GPS reading (in metres)
    accuracy_m  DECIMAL(8, 2),

    last_seen   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    INDEX idx_driver_id  (driver_id),
    INDEX idx_is_online  (is_online),
    -- Bounding-box geospatial lookup
    INDEX idx_coords     (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
