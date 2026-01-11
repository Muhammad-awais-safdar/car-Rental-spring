-- Create promotion_banners table
CREATE TABLE promotion_banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    link_url VARCHAR(500),
    button_text VARCHAR(100),
    position VARCHAR(50) DEFAULT 'HOMEPAGE',
    is_active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_is_active (is_active),
    INDEX idx_position (position),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample banners
INSERT INTO promotion_banners (title, description, image_url, link_url, button_text, position, is_active, display_order) VALUES
('New Year Sale', 'Get 20% off on all premium listings', '/banners/new-year.jpg', '/subscription/plans', 'Subscribe Now', 'HOMEPAGE', TRUE, 1),
('Featured Listings', 'Boost your listing visibility', '/banners/featured.jpg', '/promote', 'Promote Now', 'HOMEPAGE', TRUE, 2);
