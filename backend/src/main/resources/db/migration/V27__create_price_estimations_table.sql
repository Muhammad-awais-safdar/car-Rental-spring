-- Create price_estimations table
CREATE TABLE price_estimations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    mileage INT NOT NULL,
    condition_rating DECIMAL(3,2) NOT NULL,
    min_price DECIMAL(12,2) NOT NULL,
    max_price DECIMAL(12,2) NOT NULL,
    avg_price DECIMAL(12,2) NOT NULL,
    market_data JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_price_make_model (make, model),
    INDEX idx_price_year (year),
    INDEX idx_price_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
