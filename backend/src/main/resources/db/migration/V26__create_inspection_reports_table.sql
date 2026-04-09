-- Create inspection_reports table
CREATE TABLE inspection_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    listing_id BIGINT NOT NULL,
    inspector_name VARCHAR(255) NOT NULL,
    inspection_date DATE NOT NULL,
    condition_rating DECIMAL(3,2) NOT NULL,
    checklist JSON,
    photos JSON,
    issues_found TEXT,
    recommendations TEXT,
    valid_until DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_inspection_listing FOREIGN KEY (listing_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    
    INDEX idx_inspection_listing (listing_id),
    INDEX idx_inspection_valid (valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
