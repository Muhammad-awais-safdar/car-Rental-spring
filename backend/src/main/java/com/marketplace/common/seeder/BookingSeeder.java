package com.marketplace.common.seeder;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.rental.entity.Booking;
import com.marketplace.rental.entity.Rental;
import com.marketplace.rental.repository.BookingRepository;
import com.marketplace.rental.repository.RentalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BookingSeeder {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private SeederUtils utils;

    public void seed() {
        if (bookingRepository.count() > 10) {
            log.info("Bookings already seeded. Skipping...");
            return;
        }

        log.info("Seeding bookings...");

        List<User> users = userRepository.findAll().stream()
                .limit(30)
                .toList();
        List<Rental> rentals = rentalRepository.findAll();

        if (users.isEmpty() || rentals.isEmpty()) {
            log.warn("No users or rentals found. Skipping booking seeding.");
            return;
        }

        List<Booking> bookings = new ArrayList<>();
        String[] statuses = { "REQUESTED", "CONFIRMED", "ACTIVE", "COMPLETED", "CANCELLED" };

        // Create 50 bookings
        for (int i = 0; i < 50 && i < users.size() * 2; i++) {
            User user = utils.randomElement(users);
            Rental rental = utils.randomElement(rentals);

            LocalDate startDate = LocalDate.now().minusDays(utils.randomInt(1, 90));
            LocalDate endDate = startDate.plusDays(utils.randomInt(1, 14));
            String status = utils.randomElement(statuses);

            // Adjust dates based on status
            if (status.equals("COMPLETED")) {
                endDate = LocalDate.now().minusDays(utils.randomInt(1, 30));
                startDate = endDate.minusDays(utils.randomInt(1, 14));
            } else if (status.equals("CONFIRMED") || status.equals("ACTIVE")) {
                startDate = LocalDate.now().plusDays(utils.randomInt(1, 30));
                endDate = startDate.plusDays(utils.randomInt(1, 14));
            }

            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            BigDecimal totalAmount = rental.getDailyRate().multiply(BigDecimal.valueOf(days));

            Booking booking = Booking.builder()
                    .user(user)
                    .rental(rental)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalAmount(totalAmount)
                    .status(status)
                    .createdAt(LocalDateTime.now().minusDays(utils.randomInt(1, 7)))
                    .build();

            bookings.add(booking);
        }

        bookingRepository.saveAll(bookings);
        log.info("✓ Created {} bookings", bookings.size());
    }
}
