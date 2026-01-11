-- Add slug column to listings table
ALTER TABLE listings
ADD COLUMN slug VARCHAR(255) UNIQUE;

-- Generate slugs from existing titles (temporary, will be updated by application)
UPDATE listings
SET slug = LOWER(REPLACE(REPLACE(REPLACE(title, ' ', '-'), ',', ''), '.', ''))
WHERE slug IS NULL;

-- Make slug NOT NULL after populating
ALTER TABLE listings
MODIFY COLUMN slug VARCHAR(255) NOT NULL UNIQUE;

-- Create index for faster slug lookups
CREATE INDEX idx_listing_slug ON listings(slug);
