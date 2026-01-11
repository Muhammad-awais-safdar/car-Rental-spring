-- Add driver and location fields to bookings table
ALTER TABLE bookings
ADD COLUMN driver_name VARCHAR(255),
ADD COLUMN driver_email VARCHAR(255),
ADD COLUMN driver_phone VARCHAR(50),
ADD COLUMN driver_license VARCHAR(100),
ADD COLUMN needs_driver BOOLEAN DEFAULT FALSE,
ADD COLUMN pickup_location VARCHAR(500),
ADD COLUMN dropoff_location VARCHAR(500),
ADD COLUMN notes TEXT;
