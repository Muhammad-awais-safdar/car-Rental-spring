package com.marketplace.admin.service;

import com.marketplace.admin.dto.AnalyticsDTO;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.rental.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public AnalyticsDTO.OverviewStats getOverviewStats() {
        long totalUsers = userRepository.count();
        long totalListings = listingRepository.count();
        long totalBookings = bookingRepository.count();

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(monthStart);
        long newListingsThisMonth = listingRepository.countByCreatedAtAfter(monthStart);

        return AnalyticsDTO.OverviewStats.builder()
                .totalUsers(totalUsers)
                .totalListings(totalListings)
                .totalBookings(totalBookings)
                .totalRevenue(0L) // Calculate from bookings
                .activeListings(totalListings) // Filter by status
                .pendingBookings(0L) // Filter by status
                .newUsersThisMonth(newUsersThisMonth)
                .newListingsThisMonth(newListingsThisMonth)
                .build();
    }

    public AnalyticsDTO.UserGrowthStats getUserGrowthStats() {
        long totalUsers = userRepository.count();

        // Simplified - in production, calculate actual daily/monthly signups
        Map<String, Long> dailySignups = new HashMap<>();
        Map<String, Long> monthlySignups = new HashMap<>();

        return AnalyticsDTO.UserGrowthStats.builder()
                .totalUsers(totalUsers)
                .dailySignups(dailySignups)
                .monthlySignups(monthlySignups)
                .growthRate(0.0)
                .build();
    }

    public AnalyticsDTO.ListingStats getListingStats() {
        long totalListings = listingRepository.count();

        Map<String, Long> listingsByMake = new HashMap<>();
        Map<String, Long> listingsByCategory = new HashMap<>();

        return AnalyticsDTO.ListingStats.builder()
                .totalListings(totalListings)
                .activeListings(totalListings)
                .soldListings(0L)
                .listingsByCategory(listingsByCategory)
                .listingsByMake(listingsByMake)
                .build();
    }

    public AnalyticsDTO.BookingStats getBookingStats() {
        long totalBookings = bookingRepository.count();

        Map<String, Long> bookingsByMonth = new HashMap<>();

        return AnalyticsDTO.BookingStats.builder()
                .totalBookings(totalBookings)
                .completedBookings(0L)
                .cancelledBookings(0L)
                .bookingsByMonth(bookingsByMonth)
                .averageBookingValue(0.0)
                .build();
    }

    public AnalyticsDTO.RevenueStats getRevenueStats() {
        Map<String, Long> revenueByMonth = new HashMap<>();

        return AnalyticsDTO.RevenueStats.builder()
                .totalRevenue(0L)
                .revenueByMonth(revenueByMonth)
                .averageTransactionValue(0.0)
                .transactionCount(0L)
                .build();
    }
}
