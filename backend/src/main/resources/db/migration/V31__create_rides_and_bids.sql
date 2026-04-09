-- V31: Create Rides and Ride Bids Tables (RideFlex InDrive-style system)
-- This is a NEW module, separate from the existing rental/booking system

-- Create rides table (passenger requests a ride)
CREATE TABLE IF NOT EXISTS rides (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,

    -- Pickup and Dropoff
    pickup_address VARCHAR(500) NOT NULL,
    pickup_lat DECIMAL(10, 8) NOT NULL,
    pickup_lng DECIMAL(11, 8) NOT NULL,
    dropoff_address VARCHAR(500) NOT NULL,
    dropoff_lat DECIMAL(10, 8) NOT NULL,
    dropoff_lng DECIMAL(11, 8) NOT NULL,

    -- Passenger offer price (can be null = open for driver bids)
    offered_price DECIMAL(10, 2),

    -- Ride metadata
    vehicle_type VARCHAR(50) DEFAULT 'ANY', -- CAR, BIKE, VAN, ANY
    notes TEXT,
    estimated_distance_km DECIMAL(8, 2),
    estimated_duration_min INT,

    -- Accepted bid details (denormalized for quick lookup)
    accepted_bid_id BIGINT,
    assigned_driver_id BIGINT,
    final_price DECIMAL(10, 2),

    -- Commission tracking
    commission_amount DECIMAL(10, 2),
    commission_rate DECIMAL(5, 2), -- percentage
    driver_earning DECIMAL(10, 2),

    -- Lifecycle status
    status VARCHAR(30) NOT NULL DEFAULT 'SEARCHING',
    -- SEARCHING → BIDDING → ACCEPTED → DRIVER_ARRIVING → IN_PROGRESS → COMPLETED → CANCELLED

    -- Timestamps
    accepted_at TIMESTAMP NULL,
    driver_arrived_at TIMESTAMP NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason TEXT,
    cancelled_by VARCHAR(20), -- PASSENGER, DRIVER, SYSTEM

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_driver_id) REFERENCES drivers(id) ON DELETE SET NULL,

    INDEX idx_passenger (passenger_id),
    INDEX idx_driver (assigned_driver_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at),
    INDEX idx_pickup_coords (pickup_lat, pickup_lng)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create ride_bids table (drivers bid on rides)
CREATE TABLE IF NOT EXISTS ride_bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ride_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,

    -- Bid details
    bid_amount DECIMAL(10, 2) NOT NULL,
    message TEXT, -- optional message from driver to passenger
    estimated_arrival_min INT, -- driver's ETA to pickup

    -- Bid status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING → ACCEPTED | REJECTED | WITHDRAWN | EXPIRED

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,

    -- A driver can only bid once per ride
    UNIQUE KEY uq_driver_ride (driver_id, ride_id),

    INDEX idx_ride (ride_id),
    INDEX idx_driver (driver_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add FK constraint for accepted_bid_id after ride_bids table exists
ALTER TABLE rides
    ADD CONSTRAINT fk_rides_accepted_bid
    FOREIGN KEY (accepted_bid_id) REFERENCES ride_bids(id) ON DELETE SET NULL;
