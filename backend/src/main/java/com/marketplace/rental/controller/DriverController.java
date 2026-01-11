package com.marketplace.rental.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.rental.dto.DriverDTO;
import com.marketplace.rental.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.DriverResponse>> createDriver(
            @Valid @RequestBody DriverDTO.CreateDriverRequest request) {

        DriverDTO.DriverResponse response = driverService.createDriver(request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Driver created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverDTO.DriverResponse>>> getAllDrivers() {
        List<DriverDTO.DriverResponse> response = driverService.getAllDrivers();

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<DriverDTO.DriverResponse>>> getAvailableDrivers() {
        List<DriverDTO.DriverResponse> response = driverService.getAvailableDrivers();

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverDTO.DriverResponse>> getDriverById(@PathVariable Long id) {
        DriverDTO.DriverResponse response = driverService.getDriverById(id);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO.DriverResponse>> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverDTO.UpdateDriverRequest request) {

        DriverDTO.DriverResponse response = driverService.updateDriver(id, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Driver updated successfully", response),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Driver deleted successfully", null),
                HttpStatus.OK);
    }
}
