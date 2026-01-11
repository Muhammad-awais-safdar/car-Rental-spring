package com.marketplace.admin.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.listing.dto.ListingDTO;
import com.marketplace.listing.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class AdminController {

    @Autowired
    private ListingService listingService;

    @GetMapping("/listings/pending")
    public ResponseEntity<ApiResponse<Page<ListingDTO.ListingResponse>>> getPendingListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ListingDTO.ListingResponse> response = listingService.getPendingListings(page, size);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                HttpStatus.OK);
    }

    @PutMapping("/listings/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveListing(@PathVariable Long id) {
        listingService.approveListing(id);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_LISTING_APPROVED),
                HttpStatus.OK);
    }

    @PutMapping("/listings/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectListing(@PathVariable Long id) {
        listingService.rejectListing(id);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_LISTING_REJECTED),
                HttpStatus.OK);
    }
}
