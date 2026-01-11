package com.marketplace.rental.repository;

import com.marketplace.rental.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByIsAvailableTrue();

    List<Driver> findByIsAvailableTrueOrderByRatingDesc();

    boolean existsByLicenseNumber(String licenseNumber);
}
