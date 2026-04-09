-- Create analytics_events table
CREATE TABLE analytics_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSON,
    listing_id BIGINT NULL,
    session_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_analytics_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_analytics_listing FOREIGN KEY (listing_id) REFERENCES vehicles(id) ON DELETE SET NULL,
    
    INDEX idx_analytics_event_type (event_type),
    INDEX idx_analytics_listing (listing_id),
    INDEX idx_analytics_created (created_at),
    INDEX idx_analytics_user (user_id),
    INDEX idx_analytics_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
