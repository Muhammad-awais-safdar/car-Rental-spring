-- Add verification indexes to users table
-- Note: Columns is_verified, verification_level, verified_at already exist from previous migration
-- Indexes are also already created, so this migration is a no-op to maintain version continuity
SELECT 1;
