-- Extend users table for user management
-- Only add columns that don't exist (is_blocked already exists from V1)
ALTER TABLE users 
ADD COLUMN blocked_at TIMESTAMP NULL,
ADD COLUMN blocked_by BIGINT NULL,
ADD COLUMN blocked_reason TEXT NULL,
ADD CONSTRAINT fk_blocked_by FOREIGN KEY (blocked_by) REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_users_is_blocked ON users(is_blocked);
