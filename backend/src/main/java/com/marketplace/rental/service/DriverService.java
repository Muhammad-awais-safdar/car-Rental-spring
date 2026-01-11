package com.marketplace.rental.service;

import com.marketplace.auth.entity.Role;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.rental.dto.DriverDTO;
import com.marketplace.rental.entity.Driver;
import com.marketplace.rental.repository.DriverRepository;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DriverDTO.DriverResponse createDriver(DriverDTO.CreateDriverRequest request) {
        // 1. Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }

        // 2. Check if license number already exists
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BadRequestException("Driver with this license number already exists");
        }

        // 3. Create User first with DRIVER role
        User user = new User();
        // Parse full name into first and last name
        String[] nameParts = request.getName().trim().split("\\s+", 2);
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode("password")); // Default password
        user.setIsActive(true);
        user.setIsVerified(true);

        // Assign DRIVER role
        java.util.Set<Role> roles = new java.util.HashSet<>();
        roleRepository.findByName("DRIVER").ifPresent(roles::add);
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);

        // 4. Create Driver profile linked to user
        Driver driver = Driver.builder()
                .user(savedUser)
                .licenseNumber(request.getLicenseNumber())
                .licenseExpiry(request.getLicenseExpiry() != null ? LocalDate.parse(request.getLicenseExpiry()) : null)
                .yearsExperience(request.getYearsExperience())
                .bio(request.getBio())
                .profileImage(request.getProfileImage())
                .hourlyRate(request.getHourlyRate())
                .dailyRate(request.getDailyRate())
                .weeklyRate(request.getWeeklyRate())
                .languages(request.getLanguages())
                .vehicleTypes(request.getVehicleTypes())
                .isAvailable(true)
                .rating(BigDecimal.ZERO)
                .totalTrips(0)
                .build();

        driver = driverRepository.save(driver);
        return convertToResponse(driver);
    }

    public List<DriverDTO.DriverResponse> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        log.info("Drivers: {}", drivers);
        List<DriverDTO.DriverResponse> response = drivers.stream().map(this::convertToResponse)
                .collect(Collectors.toList());
        return response;
    }

    public List<DriverDTO.DriverResponse> getAvailableDrivers() {
        return driverRepository.findByIsAvailableTrueOrderByRatingDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public DriverDTO.DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        return convertToResponse(driver);
    }

    public DriverDTO.DriverResponse updateDriver(Long id, DriverDTO.UpdateDriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        // User fields (name, email, phone) should be updated in User table
        // Only update driver-specific fields here
        if (request.getBio() != null)
            driver.setBio(request.getBio());
        if (request.getProfileImage() != null)
            driver.setProfileImage(request.getProfileImage());
        if (request.getHourlyRate() != null)
            driver.setHourlyRate(request.getHourlyRate());
        if (request.getDailyRate() != null)
            driver.setDailyRate(request.getDailyRate());
        if (request.getWeeklyRate() != null)
            driver.setWeeklyRate(request.getWeeklyRate());
        if (request.getIsAvailable() != null)
            driver.setIsAvailable(request.getIsAvailable());
        if (request.getLanguages() != null)
            driver.setLanguages(request.getLanguages());
        if (request.getVehicleTypes() != null)
            driver.setVehicleTypes(request.getVehicleTypes());

        driver = driverRepository.save(driver);
        return convertToResponse(driver);
    }

    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver not found");
        }
        driverRepository.deleteById(id);
    }

    private DriverDTO.DriverResponse convertToResponse(Driver driver) {
        return DriverDTO.DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getUser().getFirstName() + " " + driver.getUser().getLastName())
                .email(driver.getUser().getEmail())
                .phone(driver.getUser().getPhone())
                .licenseNumber(driver.getLicenseNumber())
                .licenseExpiry(String.valueOf(driver.getLicenseExpiry()))
                .yearsExperience(driver.getYearsExperience())
                .bio(driver.getBio())
                .profileImage(driver.getProfileImage())
                .hourlyRate(driver.getHourlyRate())
                .dailyRate(driver.getDailyRate())
                .weeklyRate(driver.getWeeklyRate())
                .isAvailable(driver.getIsAvailable())
                .rating(driver.getRating())
                .totalTrips(driver.getTotalTrips())
                .languages(driver.getLanguages())
                .vehicleTypes(driver.getVehicleTypes())
                .createdAt(driver.getCreatedAt())
                .build();
    }
}
