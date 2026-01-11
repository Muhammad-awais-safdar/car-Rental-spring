-- Rename tables from listings to vehicles
-- This migration renames the tables for better semantic clarity

-- Rename listing_images to vehicle_images first (to avoid foreign key issues)
ALTER TABLE listing_images RENAME TO vehicle_images;

-- Update foreign key column name in vehicle_images table
ALTER TABLE vehicle_images CHANGE COLUMN listing_id vehicle_id BIGINT NOT NULL;

-- Rename listings to vehicles
ALTER TABLE listings RENAME TO vehicles;

-- Update foreign key column name in rentals table for clarity
ALTER TABLE rentals CHANGE COLUMN listing_id vehicle_id BIGINT NOT NULL;
