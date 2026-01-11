package com.marketplace.listing.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.listing.dto.ListingDTO;
import com.marketplace.listing.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

        @Autowired
        private ListingService listingService;

        @PostMapping
        @PreAuthorize("hasAnyRole('SELLER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
        public ResponseEntity<ApiResponse<ListingDTO.ListingResponse>> createListing(
                        @Valid @RequestBody ListingDTO.CreateListingRequest request,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                ListingDTO.ListingResponse response = listingService.createListing(request, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_CREATED, Constants.MSG_LISTING_CREATED, response),
                                HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('SELLER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
        public ResponseEntity<ApiResponse<ListingDTO.ListingResponse>> updateListing(
                        @PathVariable Long id,
                        @Valid @RequestBody ListingDTO.UpdateListingRequest request,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                ListingDTO.ListingResponse response = listingService.updateListing(id, request, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_LISTING_UPDATED, response),
                                HttpStatus.OK);
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<List<ListingDTO.ListingResponse>>> getUserListings(
                        @PathVariable Long userId) {
                List<ListingDTO.ListingResponse> listings = listingService.getOwnerListings(userId);
                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, listings));
        }

        @GetMapping("/{slug}")
        public ResponseEntity<ApiResponse<ListingDTO.ListingResponse>> getListingBySlug(
                        @PathVariable String slug) {
                ListingDTO.ListingResponse listing = listingService.getListingBySlug(slug);
                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, listing));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('SELLER', 'CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
        public ResponseEntity<ApiResponse<Void>> deleteListing(
                        @PathVariable Long id,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                listingService.deleteListing(id, userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_LISTING_DELETED),
                                HttpStatus.OK);
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<Page<ListingDTO.ListingResponse>>> searchListings(
                        @RequestParam(required = false) String make,
                        @RequestParam(required = false) String model,
                        @RequestParam(required = false) java.math.BigDecimal minPrice,
                        @RequestParam(required = false) java.math.BigDecimal maxPrice,
                        @RequestParam(required = false) Integer minYear,
                        @RequestParam(required = false) Integer maxYear,
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) String listingType,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String sortBy,
                        @RequestParam(required = false) String sortOrder,
                        @RequestParam(defaultValue = "0") Integer page,
                        @RequestParam(defaultValue = "10") Integer size) {

                ListingDTO.ListingSearchRequest searchRequest = ListingDTO.ListingSearchRequest.builder()
                                .make(make)
                                .model(model)
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .minYear(minYear)
                                .maxYear(maxYear)
                                .location(location)
                                .listingType(listingType)
                                .status(status)
                                .sortBy(sortBy)
                                .sortOrder(sortOrder)
                                .page(page)
                                .size(size)
                                .build();

                Page<ListingDTO.ListingResponse> response = listingService.searchListings(searchRequest);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }

        @GetMapping("/my")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<ApiResponse<List<ListingDTO.ListingResponse>>> getMyListings(
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                List<ListingDTO.ListingResponse> response = listingService.getOwnerListings(userId);

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_SUCCESS, Constants.MSG_SUCCESS, response),
                                HttpStatus.OK);
        }
}
