package com.marketplace.analytics.controller;

import com.marketplace.analytics.dto.AnalyticsDTO;
import com.marketplace.analytics.service.AnalyticsService;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/track")
    public ResponseEntity<ApiResponse<Void>> trackEvent(
            @RequestBody AnalyticsDTO.TrackEventRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {

        Long userId = authentication != null ? Long.parseLong(authentication.getPrincipal().toString()) : null;

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        analyticsService.trackEvent(request, userId, ipAddress, userAgent);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Event tracked", null));
    }

    @GetMapping("/listing/{id}")
    public ResponseEntity<ApiResponse<AnalyticsDTO.ListingAnalyticsResponse>> getListingAnalytics(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        if (start == null)
            start = LocalDateTime.now().minusDays(30);
        if (end == null)
            end = LocalDateTime.now();

        AnalyticsDTO.ListingAnalyticsResponse response = analyticsService.getListingAnalytics(id, start, end);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Listing analytics retrieved", response));
    }

    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<AnalyticsDTO.SellerAnalyticsResponse>> getSellerAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication authentication) {

        if (start == null)
            start = LocalDateTime.now().minusDays(30);
        if (end == null)
            end = LocalDateTime.now();

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        AnalyticsDTO.SellerAnalyticsResponse response = analyticsService.getSellerAnalytics(userId, start, end);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Seller analytics retrieved", response));
    }

    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsDTO.PlatformAnalyticsResponse>> getPlatformAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        if (start == null)
            start = LocalDateTime.now().minusDays(30);
        if (end == null)
            end = LocalDateTime.now();

        AnalyticsDTO.PlatformAnalyticsResponse response = analyticsService.getPlatformAnalytics(start, end);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Platform analytics retrieved", response));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<AnalyticsDTO.PopularListingsResponse>> getPopularListings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        if (start == null)
            start = LocalDateTime.now().minusDays(7);
        if (end == null)
            end = LocalDateTime.now();

        AnalyticsDTO.PopularListingsResponse response = analyticsService.getPopularListings(start, end);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Popular listings retrieved", response));
    }
}
