package com.marketplace.wishlist.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.wishlist.dto.WishlistDTO;
import com.marketplace.wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

        @Autowired
        private WishlistService wishlistService;

        @PostMapping
        public ResponseEntity<ApiResponse<WishlistDTO.WishlistResponse>> addToWishlist(
                        @RequestBody WishlistDTO.AddToWishlistRequest request,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                WishlistDTO.WishlistResponse response = wishlistService.addToWishlist(userId, request.getListingId());

                return new ResponseEntity<>(
                                ApiResponse.success(Constants.STATUS_CREATED, "Added to wishlist", response),
                                HttpStatus.CREATED);
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<WishlistDTO.WishlistResponse>>> getUserWishlist(
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                List<WishlistDTO.WishlistResponse> wishlist = wishlistService.getUserWishlist(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, "Wishlist retrieved successfully",
                                                wishlist));
        }

        @DeleteMapping("/{listingId}")
        public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
                        @PathVariable Long listingId,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                wishlistService.removeFromWishlist(userId, listingId);

                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, "Removed from wishlist", null));
        }

        @GetMapping("/check/{listingId}")
        public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
                        @PathVariable Long listingId,
                        Authentication authentication) {

                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                boolean inWishlist = wishlistService.isInWishlist(userId, listingId);

                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, "Wishlist status checked", inWishlist));
        }

        @GetMapping("/count")
        public ResponseEntity<ApiResponse<Long>> getWishlistCount(Authentication authentication) {
                Long userId = Long.parseLong(authentication.getPrincipal().toString());
                long count = wishlistService.getWishlistCount(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(Constants.STATUS_SUCCESS, "Wishlist count retrieved", count));
        }
}
