package com.marketplace.wishlist.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.wishlist.dto.WishlistDTO;
import com.marketplace.wishlist.entity.Wishlist;
import com.marketplace.wishlist.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    public WishlistDTO.WishlistResponse addToWishlist(Long userId, Long listingId) {
        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndVehicleId(userId, listingId)) {
            throw new BadRequestException("Listing already in wishlist");
        }

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate listing
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Create wishlist entry
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .vehicle(listing)
                .build();

        wishlist = wishlistRepository.save(wishlist);
        return convertToResponse(wishlist);
    }

    public void removeFromWishlist(Long userId, Long listingId) {
        if (!wishlistRepository.existsByUserIdAndVehicleId(userId, listingId)) {
            throw new ResourceNotFoundException("Listing not in wishlist");
        }
        wishlistRepository.deleteByUserIdAndVehicleId(userId, listingId);
    }

    public List<WishlistDTO.WishlistResponse> getUserWishlist(Long userId) {
        List<Wishlist> wishlistItems = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return wishlistItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public boolean isInWishlist(Long userId, Long listingId) {
        return wishlistRepository.existsByUserIdAndVehicleId(userId, listingId);
    }

    public long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    private WishlistDTO.WishlistResponse convertToResponse(Wishlist wishlist) {
        Listing listing = wishlist.getVehicle();
        String image = listing.getImages().isEmpty()
                ? null
                : listing.getImages().get(0).getImageUrl();

        return WishlistDTO.WishlistResponse.builder()
                .id(wishlist.getId())
                .listingId(listing.getId())
                .listingTitle(listing.getTitle())
                .listingImage(image)
                .price(listing.getPrice())
                .location(listing.getLocation())
                .status(listing.getStatus())
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}
