package com.marketplace.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistResponse {
        private Long id;
        private Long listingId;
        private String listingTitle;
        private String listingImage;
        private BigDecimal price;
        private String location;
        private String status;
        private LocalDateTime addedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToWishlistRequest {
        private Long listingId;
    }
}
