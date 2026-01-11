-- Add featured listing columns to vehicles table
-- is_featured already exists, only add new columns
ALTER TABLE vehicles 
ADD COLUMN featured_until TIMESTAMP NULL,
ADD COLUMN boost_level INT DEFAULT 0;

CREATE INDEX idx_featured ON vehicles(is_featured, featured_until);
CREATE INDEX idx_boost_level ON vehicles(boost_level);
