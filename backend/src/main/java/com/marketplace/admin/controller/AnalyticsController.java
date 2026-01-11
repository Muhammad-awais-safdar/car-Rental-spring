package com.marketplace.admin.controller;

import com.marketplace.admin.dto.AnalyticsDTO;
import com.marketplace.admin.service.AnalyticsService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AnalyticsDTO.OverviewStats>> getOverviewStats() {
        AnalyticsDTO.OverviewStats stats = analyticsService.getOverviewStats();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Overview statistics retrieved successfully", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<AnalyticsDTO.UserGrowthStats>> getUserGrowthStats() {
        AnalyticsDTO.UserGrowthStats stats = analyticsService.getUserGrowthStats();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User growth statistics retrieved successfully", stats));
    }

    @GetMapping("/listings")
    public ResponseEntity<ApiResponse<AnalyticsDTO.ListingStats>> getListingStats() {
        AnalyticsDTO.ListingStats stats = analyticsService.getListingStats();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Listing statistics retrieved successfully", stats));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<AnalyticsDTO.BookingStats>> getBookingStats() {
        AnalyticsDTO.BookingStats stats = analyticsService.getBookingStats();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Booking statistics retrieved successfully", stats));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<AnalyticsDTO.RevenueStats>> getRevenueStats() {
        AnalyticsDTO.RevenueStats stats = analyticsService.getRevenueStats();
        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Revenue statistics retrieved successfully", stats));
    }
}
