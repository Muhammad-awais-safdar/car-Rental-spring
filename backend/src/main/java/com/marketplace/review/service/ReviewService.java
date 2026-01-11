package com.marketplace.review.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.review.dto.ReviewDTO;
import com.marketplace.review.entity.Review;
import com.marketplace.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    public ReviewDTO.ReviewResponse createReview(Long userId, ReviewDTO.CreateReviewRequest request) {
        // Check if user already reviewed this listing
        if (reviewRepository.existsByUserIdAndVehicleId(userId, request.getListingId())) {
            throw new BadRequestException("You have already reviewed this listing");
        }

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate listing
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Create review
        Review review = Review.builder()
                .user(user)
                .vehicle(listing)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    public ReviewDTO.ReviewResponse updateReview(Long reviewId, Long userId, ReviewDTO.UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own reviews");
        }

        // Update fields
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    public Page<ReviewDTO.ReviewResponse> getListingReviews(Long listingId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByVehicleIdOrderByCreatedAtDesc(listingId, pageable);
        return reviews.map(this::convertToResponse);
    }

    public List<ReviewDTO.ReviewResponse> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ReviewDTO.RatingStatistics getListingRatingStatistics(Long listingId) {
        Double averageRating = reviewRepository.getAverageRatingByVehicleId(listingId);
        long totalReviews = reviewRepository.countByVehicleId(listingId);

        // Get rating distribution
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = reviewRepository.countByVehicleIdAndRating(listingId, i);
            distribution.put(i, count);
        }

        return ReviewDTO.RatingStatistics.builder()
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews)
                .ratingDistribution(distribution)
                .build();
    }

    private ReviewDTO.ReviewResponse convertToResponse(Review review) {
        return ReviewDTO.ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .listingId(review.getVehicle().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
