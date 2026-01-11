package com.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot Application for Car Marketplace and Rental Platform
 */
@SpringBootApplication
@EnableJpaAuditing
public class
CarMarketplaceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CarMarketplaceApplication.class, args);
    }
}
