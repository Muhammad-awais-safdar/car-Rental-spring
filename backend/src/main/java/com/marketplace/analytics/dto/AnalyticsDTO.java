package com.marketplace.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackEventRequest {
        private String eventType;
        private Long listingId;
        private Map<String, Object> eventData;
        private String sessionId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingAnalyticsResponse {
        private Long listingId;
        private String listingTitle;
        private Long totalViews;
        private Long totalClicks;
        private Long totalBookings;
        private Double conversionRate;
        private List<DailyMetric> dailyViews;
        private List<DailyMetric> dailyClicks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerAnalyticsResponse {
        private Long totalViews;
        private Long totalClicks;
        private Long totalBookings;
        private Long totalRevenue;
        private Double averageConversionRate;
        private List<ListingPerformance> topListings;
        private List<DailyMetric> viewsTrend;
        private List<DailyMetric> bookingsTrend;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformAnalyticsResponse {
        private Long totalUsers;
        private Long activeListings;
        private Long totalBookings;
        private Long totalRevenue;
        private Double userGrowthRate;
        private List<DailyMetric> userGrowth;
        private List<DailyMetric> bookingsTrend;
        private List<DailyMetric> revenueTrend;
        private List<CategoryMetric> popularCategories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListingPerformance {
        private Long listingId;
        private String title;
        private Long views;
        private Long clicks;
        private Long bookings;
        private Double conversionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetric {
        private LocalDate date;
        private Long count;
        private Double value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryMetric {
        private String category;
        private Long count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchTrendsResponse {
        private List<SearchTerm> topSearches;
        private List<String> recentSearches;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchTerm {
        private String term;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularListingsResponse {
        private List<ListingPerformance> mostViewed;
        private List<ListingPerformance> mostBooked;
        private List<ListingPerformance> trending;
    }
}
