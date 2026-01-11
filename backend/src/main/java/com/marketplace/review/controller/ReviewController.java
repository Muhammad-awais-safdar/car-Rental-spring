package com.marketplace.review.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.review.dto.ReviewDTO;
import com.marketplace.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO.ReviewResponse>> createReview(
            @Valid @RequestBody ReviewDTO.CreateReviewRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        ReviewDTO.ReviewResponse response = reviewService.createReview(userId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Review created successfully", response),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO.ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDTO.UpdateReviewRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        ReviewDTO.ReviewResponse response = reviewService.updateReview(id, userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Review updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        reviewService.deleteReview(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Review deleted successfully", null));
    }

    @GetMapping("/listings/{listingId}")
    public ResponseEntity<ApiResponse<Page<ReviewDTO.ReviewResponse>>> getListingReviews(
            @PathVariable Long listingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO.ReviewResponse> reviews = reviewService.getListingReviews(listingId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Reviews retrieved successfully", reviews));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReviewDTO.ReviewResponse>>> getUserReviews(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<ReviewDTO.ReviewResponse> reviews = reviewService.getUserReviews(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "User reviews retrieved successfully", reviews));
    }

    @GetMapping("/listings/{listingId}/statistics")
    public ResponseEntity<ApiResponse<ReviewDTO.RatingStatistics>> getListingStatistics(
            @PathVariable Long listingId) {

        ReviewDTO.RatingStatistics statistics = reviewService.getListingRatingStatistics(listingId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Statistics retrieved successfully", statistics));
    }
}
