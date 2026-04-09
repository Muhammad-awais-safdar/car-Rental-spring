package com.marketplace.common.seeder;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.notification.entity.Notification;
import com.marketplace.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NotificationSeeder {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeederUtils utils;

    private static final String[] NOTIFICATION_TYPES = { "BOOKING", "MESSAGE", "REVIEW", "SYSTEM", "PAYMENT" };

    private static final String[] TITLES = {
            "New Booking Request",
            "Booking Confirmed",
            "New Message Received",
            "Review Posted",
            "Payment Received",
            "Account Verified",
            "Listing Approved",
            "Price Drop Alert",
            "Booking Reminder",
            "System Update"
    };

    private static final String[] MESSAGES = {
            "You have a new booking request for your listing.",
            "Your booking has been confirmed by the seller.",
            "You have received a new message from a buyer.",
            "A new review has been posted on your listing.",
            "Payment has been successfully processed.",
            "Your account has been verified.",
            "Your listing has been approved and is now live.",
            "A car you're interested in has a price drop!",
            "Reminder: Your booking starts tomorrow.",
            "System maintenance scheduled for tonight."
    };

    public void seed() {
        if (notificationRepository.count() > 20) {
            log.info("Notifications already seeded. Skipping...");
            return;
        }

        log.info("Seeding notifications...");

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.warn("No users found. Skipping notification seeding.");
            return;
        }

        List<Notification> notifications = new ArrayList<>();

        // Create 80 notifications
        for (int i = 0; i < 80; i++) {
            User user = utils.randomElement(users);
            String type = utils.randomElement(NOTIFICATION_TYPES);
            int index = utils.randomInt(0, TITLES.length - 1);

            Notification notification = Notification.builder()
                    .user(user)
                    .type(type)
                    .title(TITLES[index])
                    .message(MESSAGES[index])
                    .isRead(utils.randomBoolean(0.6)) // 60% read
                    .createdAt(utils.randomPastDate(30))
                    .build();

            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
        log.info("✓ Created {} notifications", notifications.size());
    }
}
