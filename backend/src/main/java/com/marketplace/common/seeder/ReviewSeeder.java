package com.marketplace.common.seeder;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.review.entity.Review;
import com.marketplace.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ReviewSeeder {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private SeederUtils utils;

    private static final String[] POSITIVE_REVIEWS = {
            "Great car! Very clean and well-maintained. Highly recommend!",
            "Excellent experience. The car was exactly as described.",
            "Smooth transaction. The seller was very professional and helpful.",
            "Amazing vehicle! Runs perfectly and looks brand new.",
            "Very satisfied with this purchase. Would buy from this seller again!",
            "Outstanding service and quality. Five stars all the way!",
            "The car exceeded my expectations. Absolutely love it!",
            "Perfect condition and great price. Couldn't be happier!"
    };

    private static final String[] NEUTRAL_REVIEWS = {
            "Good car overall. A few minor issues but nothing major.",
            "Decent experience. The car is okay for the price.",
            "Average vehicle. Met my basic needs.",
            "Fair deal. Some wear and tear but expected for the age.",
            "Acceptable condition. Would have preferred better communication."
    };

    private static final String[] NEGATIVE_REVIEWS = {
            "Not as described. Several issues that weren't mentioned.",
            "Disappointed with the condition. Needs repairs.",
            "Poor communication from seller. Would not recommend.",
            "Car had hidden problems. Not worth the price.",
            "Unsatisfactory experience. Expected better quality."
    };

    public void seed() {
        if (reviewRepository.count() > 10) {
            log.info("Reviews already seeded. Skipping...");
            return;
        }

        log.info("Seeding reviews...");

        List<User> buyers = userRepository.findAll().stream()
                .limit(30)
                .toList();
        List<Listing> listings = listingRepository.findAll();

        if (buyers.isEmpty() || listings.isEmpty()) {
            log.warn("No buyers or listings found. Skipping review seeding.");
            return;
        }

        List<Review> reviews = new ArrayList<>();

        // Create 60 reviews with realistic rating distribution
        for (int i = 0; i < 60 && i < listings.size(); i++) {
            User reviewer = utils.randomElement(buyers);
            Listing listing = utils.randomElement(listings);

            // Weighted rating distribution (more 4-5 stars)
            int rating = utils.weightedRandom(
                    List.of(1, 2, 3, 4, 5),
                    List.of(2.0, 3.0, 10.0, 35.0, 50.0));

            String comment = getCommentForRating(rating);

            Review review = Review.builder()
                    .user(reviewer)
                    .vehicle(listing)
                    .rating(rating)
                    .comment(comment)
                    .createdAt(utils.randomPastDate(180))
                    .build();

            reviews.add(review);
        }

        reviewRepository.saveAll(reviews);
        log.info("✓ Created {} reviews", reviews.size());
    }

    private String getCommentForRating(int rating) {
        if (rating >= 4) {
            return utils.randomElement(POSITIVE_REVIEWS);
        } else if (rating == 3) {
            return utils.randomElement(NEUTRAL_REVIEWS);
        } else {
            return utils.randomElement(NEGATIVE_REVIEWS);
        }
    }
}
