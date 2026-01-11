-- Create makes table
CREATE TABLE makes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create models table
CREATE TABLE models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (make_id) REFERENCES makes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_make_model (make_id, name),
    INDEX idx_make_id (make_id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert some common makes
INSERT INTO makes (name) VALUES 
('Toyota'), ('Honda'), ('Ford'), ('Chevrolet'), ('BMW'), 
('Mercedes-Benz'), ('Audi'), ('Volkswagen'), ('Nissan'), ('Hyundai'),
('Kia'), ('Mazda'), ('Subaru'), ('Lexus'), ('Porsche'),
('Tesla'), ('Volvo'), ('Jaguar'), ('Land Rover'), ('Jeep');
