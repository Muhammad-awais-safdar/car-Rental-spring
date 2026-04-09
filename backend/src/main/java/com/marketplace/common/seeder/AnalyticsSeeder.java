package com.marketplace.common.seeder;

import com.marketplace.analytics.entity.AnalyticsEvent;
import com.marketplace.analytics.repository.AnalyticsEventRepository;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AnalyticsSeeder {

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private SeederUtils utils;

    public void seed() {
        if (analyticsEventRepository.count() > 50) {
            log.info("Analytics events already seeded. Skipping...");
            return;
        }

        log.info("Seeding analytics events...");

        List<User> users = userRepository.findAll();
        List<Listing> listings = listingRepository.findAll();

        if (users.isEmpty() || listings.isEmpty()) {
            log.warn("No users or listings found. Skipping analytics seeding.");
            return;
        }

        List<AnalyticsEvent> events = new ArrayList<>();
        String[] eventTypes = { "VIEW", "SEARCH", "CLICK", "BOOKING", "MESSAGE", "SHARE" };
        List<Double> eventWeights = List.of(40.0, 25.0, 20.0, 5.0, 8.0, 2.0); // Realistic distribution

        // Create 300 events over last 60 days
        for (int i = 0; i < 300; i++) {
            User user = utils.randomBoolean(0.7) ? utils.randomElement(users) : null; // 30% anonymous
            Listing listing = utils.randomElement(listings);
            String eventType = utils.weightedRandom(List.of(eventTypes), eventWeights);

            AnalyticsEvent event = AnalyticsEvent.builder()
                    .user(user)
                    .listing(listing)
                    .eventType(eventType)
                    .eventData("{\"source\":\"web\",\"device\":\"desktop\"}")
                    .sessionId(UUID.randomUUID().toString())
                    .ipAddress(generateRandomIP())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .referrer(utils.randomBoolean(0.3) ? "https://google.com" : null)
                    .createdAt(utils.randomPastDate(60))
                    .build();

            events.add(event);
        }

        analyticsEventRepository.saveAll(events);
        log.info("✓ Created {} analytics events", events.size());
    }

    private String generateRandomIP() {
        return utils.randomInt(1, 255) + "." +
                utils.randomInt(0, 255) + "." +
                utils.randomInt(0, 255) + "." +
                utils.randomInt(1, 255);
    }
}
