-- Create drivers table with user relationship
CREATE TABLE drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    license_expiry DATE,
    years_experience INT DEFAULT 0,
    bio TEXT,
    profile_image VARCHAR(500),
    
    -- Pricing
    hourly_rate DECIMAL(10, 2),
    daily_rate DECIMAL(10, 2),
    weekly_rate DECIMAL(10, 2),
    
    -- Availability and Rating
    is_available BOOLEAN DEFAULT TRUE,
    rating DECIMAL(3, 2) DEFAULT 0.00,
    total_trips INT DEFAULT 0,
    
    -- Additional info
    languages VARCHAR(255),
    vehicle_types VARCHAR(255),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add driver_id to bookings table
ALTER TABLE bookings
ADD COLUMN driver_id BIGINT,
ADD CONSTRAINT fk_booking_driver FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE SET NULL;
