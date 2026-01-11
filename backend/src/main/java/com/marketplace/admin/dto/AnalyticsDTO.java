package com.marketplace.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

public class AnalyticsDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewStats {
        private Long totalUsers;
        private Long totalListings;
        private Long totalBookings;
        private Long totalRevenue;
        private Long activeListings;
        private Long pendingBookings;
        private Long newUsersThisMonth;
        private Long newListingsThisMonth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGrowthStats {
        private Map<String, Long> dailySignups;
        private Map<String, Long> monthlySignups;
        private Long totalUsers;
        private Double growthRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingStats {
        private Long totalListings;
        private Long activeListings;
        private Long soldListings;
        private Map<String, Long> listingsByCategory;
        private Map<String, Long> listingsByMake;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingStats {
        private Long totalBookings;
        private Long completedBookings;
        private Long cancelledBookings;
        private Map<String, Long> bookingsByMonth;
        private Double averageBookingValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStats {
        private Long totalRevenue;
        private Map<String, Long> revenueByMonth;
        private Double averageTransactionValue;
        private Long transactionCount;
    }
}
