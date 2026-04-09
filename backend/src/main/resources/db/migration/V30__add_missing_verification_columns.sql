-- Add missing verification columns to users table
ALTER TABLE users 
ADD COLUMN verification_level VARCHAR(50) DEFAULT 'NONE',
ADD COLUMN verified_at TIMESTAMP NULL;

-- Add indexes for verification fields
CREATE INDEX idx_users_verified ON users(is_verified);
CREATE INDEX idx_users_verification_level ON users(verification_level);
