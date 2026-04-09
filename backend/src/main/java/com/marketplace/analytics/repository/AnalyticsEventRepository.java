package com.marketplace.analytics.repository;

import com.marketplace.analytics.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    Long countByEventTypeAndCreatedAtBetween(String eventType, LocalDateTime start, LocalDateTime end);

    Long countByListingIdAndEventType(Long listingId, String eventType);

    Long countByListingIdAndEventTypeAndCreatedAtBetween(
            Long listingId, String eventType, LocalDateTime start, LocalDateTime end);

    List<AnalyticsEvent> findByUserIdAndEventTypeOrderByCreatedAtDesc(Long userId, String eventType);

    @Query("SELECT e.listing.id, COUNT(e) FROM AnalyticsEvent e " +
            "WHERE e.eventType = :eventType AND e.createdAt BETWEEN :start AND :end " +
            "GROUP BY e.listing.id ORDER BY COUNT(e) DESC")
    List<Object[]> findTopListingsByEventType(
            @Param("eventType") String eventType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT DATE(e.createdAt), COUNT(e) FROM AnalyticsEvent e " +
            "WHERE e.eventType = :eventType AND e.createdAt BETWEEN :start AND :end " +
            "GROUP BY DATE(e.createdAt) ORDER BY DATE(e.createdAt)")
    List<Object[]> getEventCountsByDate(
            @Param("eventType") String eventType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT e.eventData FROM AnalyticsEvent e " +
            "WHERE e.eventType = 'SEARCH' AND e.createdAt BETWEEN :start AND :end " +
            "ORDER BY e.createdAt DESC")
    List<String> getRecentSearchQueries(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
