package com.marketplace.rental.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.rental.dto.RentalDTO;
import com.marketplace.rental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'RENTER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RentalDTO.RentalResponse>> createRental(
            @Valid @RequestBody RentalDTO.CreateRentalRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RentalDTO.RentalResponse response = rentalService.createRental(request, userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_RENTAL_CREATED, response),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'RENTER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<RentalDTO.RentalResponse>> updateRental(
            @PathVariable Long id,
            @Valid @RequestBody RentalDTO.UpdateRentalRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        RentalDTO.RentalResponse response = rentalService.updateRental(id, request, userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RENTAL_UPDATED, response),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'RENTER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRental(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        rentalService.deleteRental(id, userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_RENTAL_DELETED),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RentalDTO.RentalResponse>> getRentalById(@PathVariable Long id) {
        RentalDTO.RentalResponse response = rentalService.getRentalById(id);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<ApiResponse<RentalDTO.RentalResponse>> getRentalByListingId(@PathVariable Long listingId) {
        RentalDTO.RentalResponse response = rentalService.getRentalByListingId(listingId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<RentalDTO.RentalResponse>>> getUserRentals(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<RentalDTO.RentalResponse> response = rentalService.getUserRentals(userId);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @PostMapping("/{id}/check-availability")
    public ResponseEntity<ApiResponse<RentalDTO.AvailabilityResponse>> checkAvailability(
            @PathVariable Long id,
            @Valid @RequestBody RentalDTO.AvailabilityRequest request) {

        RentalDTO.AvailabilityResponse response = rentalService.checkAvailability(id, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }
}
