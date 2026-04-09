package com.marketplace.analytics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.analytics.dto.AnalyticsDTO;
import com.marketplace.analytics.entity.AnalyticsEvent;
import com.marketplace.analytics.repository.AnalyticsEventRepository;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void trackEvent(AnalyticsDTO.TrackEventRequest request, Long userId, String ipAddress, String userAgent) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        Listing listing = request.getListingId() != null
                ? listingRepository.findById(request.getListingId()).orElse(null)
                : null;

        String eventDataJson = null;
        if (request.getEventData() != null) {
            try {
                eventDataJson = objectMapper.writeValueAsString(request.getEventData());
            } catch (JsonProcessingException e) {
                // Handle silently
            }
        }

        AnalyticsEvent event = AnalyticsEvent.builder()
                .user(user)
                .eventType(request.getEventType())
                .eventData(eventDataJson)
                .listing(listing)
                .sessionId(request.getSessionId())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        analyticsEventRepository.save(event);
    }

    public AnalyticsDTO.ListingAnalyticsResponse getListingAnalytics(Long listingId, LocalDateTime start,
            LocalDateTime end) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        Long totalViews = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                listingId, "VIEW", start, end);
        Long totalClicks = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                listingId, "CLICK", start, end);
        Long totalBookings = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                listingId, "BOOKING", start, end);

        Double conversionRate = totalViews > 0 ? (totalBookings.doubleValue() / totalViews.doubleValue()) * 100 : 0.0;

        List<Object[]> dailyViewsData = analyticsEventRepository.getEventCountsByDate("VIEW", start, end);
        List<AnalyticsDTO.DailyMetric> dailyViews = dailyViewsData.stream()
                .map(row -> AnalyticsDTO.DailyMetric.builder()
                        .date((LocalDate) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDTO.ListingAnalyticsResponse.builder()
                .listingId(listingId)
                .listingTitle(listing.getTitle())
                .totalViews(totalViews)
                .totalClicks(totalClicks)
                .totalBookings(totalBookings)
                .conversionRate(BigDecimal.valueOf(conversionRate).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .dailyViews(dailyViews)
                .build();
    }

    public AnalyticsDTO.SellerAnalyticsResponse getSellerAnalytics(Long userId, LocalDateTime start,
            LocalDateTime end) {
        List<Listing> sellerListings = listingRepository
                .findByOwnerId(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        List<Long> listingIds = sellerListings.stream().map(Listing::getId).collect(Collectors.toList());

        long totalViews = 0;
        long totalClicks = 0;
        long totalBookings = 0;

        List<AnalyticsDTO.ListingPerformance> topListings = new ArrayList<>();

        for (Listing listing : sellerListings) {
            Long views = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                    listing.getId(), "VIEW", start, end);
            Long clicks = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                    listing.getId(), "CLICK", start, end);
            Long bookings = analyticsEventRepository.countByListingIdAndEventTypeAndCreatedAtBetween(
                    listing.getId(), "BOOKING", start, end);

            totalViews += views;
            totalClicks += clicks;
            totalBookings += bookings;

            Double conversionRate = views > 0 ? (bookings.doubleValue() / views.doubleValue()) * 100 : 0.0;

            topListings.add(AnalyticsDTO.ListingPerformance.builder()
                    .listingId(listing.getId())
                    .title(listing.getTitle())
                    .views(views)
                    .clicks(clicks)
                    .bookings(bookings)
                    .conversionRate(BigDecimal.valueOf(conversionRate).setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .build());
        }

        // Sort by views and take top 5
        topListings.sort((a, b) -> Long.compare(b.getViews(), a.getViews()));
        topListings = topListings.stream().limit(5).collect(Collectors.toList());

        Double averageConversionRate = totalViews > 0 ? (totalBookings * 100.0 / totalViews) : 0.0;

        return AnalyticsDTO.SellerAnalyticsResponse.builder()
                .totalViews(totalViews)
                .totalClicks(totalClicks)
                .totalBookings(totalBookings)
                .totalRevenue(0L) // Calculate from bookings
                .averageConversionRate(
                        BigDecimal.valueOf(averageConversionRate).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .topListings(topListings)
                .build();
    }

    public AnalyticsDTO.PlatformAnalyticsResponse getPlatformAnalytics(LocalDateTime start, LocalDateTime end) {
        Long totalUsers = userRepository.count();
        Long activeListings = listingRepository.count();
        Long totalBookings = analyticsEventRepository.countByEventTypeAndCreatedAtBetween("BOOKING", start, end);

        Long newUsers = userRepository.countByCreatedAtBetween(start, end);
        Long previousPeriodUsers = userRepository.countByCreatedAtBetween(
                start.minusDays(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay()),
                start);

        Double userGrowthRate = previousPeriodUsers > 0
                ? ((newUsers - previousPeriodUsers) * 100.0 / previousPeriodUsers)
                : 0.0;

        return AnalyticsDTO.PlatformAnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .activeListings(activeListings)
                .totalBookings(totalBookings)
                .totalRevenue(0L)
                .userGrowthRate(BigDecimal.valueOf(userGrowthRate).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .build();
    }

    public AnalyticsDTO.PopularListingsResponse getPopularListings(LocalDateTime start, LocalDateTime end) {
        List<Object[]> mostViewedData = analyticsEventRepository.findTopListingsByEventType("VIEW", start, end);
        List<AnalyticsDTO.ListingPerformance> mostViewed = convertToListingPerformance(mostViewedData, "VIEW");

        List<Object[]> mostBookedData = analyticsEventRepository.findTopListingsByEventType("BOOKING", start, end);
        List<AnalyticsDTO.ListingPerformance> mostBooked = convertToListingPerformance(mostBookedData, "BOOKING");

        return AnalyticsDTO.PopularListingsResponse.builder()
                .mostViewed(mostViewed.stream().limit(10).collect(Collectors.toList()))
                .mostBooked(mostBooked.stream().limit(10).collect(Collectors.toList()))
                .trending(mostViewed.stream().limit(5).collect(Collectors.toList()))
                .build();
    }

    private List<AnalyticsDTO.ListingPerformance> convertToListingPerformance(List<Object[]> data, String eventType) {
        return data.stream()
                .map(row -> {
                    Long listingId = ((Number) row[0]).longValue();
                    Long count = ((Number) row[1]).longValue();

                    Listing listing = listingRepository.findById(listingId).orElse(null);
                    if (listing == null)
                        return null;

                    return AnalyticsDTO.ListingPerformance.builder()
                            .listingId(listingId)
                            .title(listing.getTitle())
                            .views(eventType.equals("VIEW") ? count : 0L)
                            .bookings(eventType.equals("BOOKING") ? count : 0L)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
