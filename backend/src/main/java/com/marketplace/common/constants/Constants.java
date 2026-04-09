package com.marketplace.common.constants;

import java.math.BigDecimal;

/**
 * Centralized constants for the application
 */
public class Constants {

    // Status Codes
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_ERROR = 500;

    // Messages
    public static final String MSG_SUCCESS = "Success";
    public static final String MSG_CREATED = "Created successfully";
    public static final String MSG_NOT_FOUND = "Resource not found";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_FORBIDDEN = "Access forbidden";

    // Auth Messages
    public static final String MSG_REGISTER_SUCCESS = "Registration successful";
    public static final String MSG_LOGIN_SUCCESS = "Login successful";
    public static final String MSG_INVALID_CREDENTIALS = "Invalid email/phone or password";
    public static final String MSG_EMAIL_EXISTS = "Email already registered";

    // Listing Messages
    public static final String MSG_LISTING_CREATED = "Listing created successfully";
    public static final String MSG_LISTING_UPDATED = "Listing updated successfully";
    public static final String MSG_LISTING_DELETED = "Listing deleted successfully";
    public static final String MSG_LISTING_APPROVED = "Listing approved";
    public static final String MSG_LISTING_REJECTED = "Listing rejected";

    // Rental Messages
    public static final String MSG_RENTAL_CREATED = "Rental created successfully";
    public static final String MSG_RENTAL_UPDATED = "Rental updated successfully";
    public static final String MSG_RENTAL_DELETED = "Rental deleted successfully";

    // Booking Messages
    public static final String MSG_BOOKING_CREATED = "Booking created successfully";
    public static final String MSG_BOOKING_CONFIRMED = "Booking confirmed";
    public static final String MSG_BOOKING_CANCELLED = "Booking cancelled";

    // Roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SELLER = "SELLER";
    public static final String ROLE_BUYER = "BUYER";
    public static final String ROLE_RENTER = "RENTER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_DRIVER = "DRIVER";
    public static final String ROLE_PASSENGER = "PASSENGER";
    public static final String ROLE_MANAGER = "MANAGER";

    // Listing Status
    public static final String LISTING_STATUS_PENDING = "PENDING";
    public static final String LISTING_STATUS_APPROVED = "APPROVED";
    public static final String LISTING_STATUS_REJECTED = "REJECTED";

    // Listing Type
    public static final String LISTING_TYPE_SELL = "SELL";
    public static final String LISTING_TYPE_RENT = "RENT";

    // Booking Status
    public static final String BOOKING_STATUS_REQUESTED = "REQUESTED";
    public static final String BOOKING_STATUS_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_STATUS_CANCELLED = "CANCELLED";
    public static final String BOOKING_STATUS_COMPLETED = "COMPLETED";

    // ─── RIDE STATUS ─────────────────────────────────────────────────────────
    public static final String RIDE_STATUS_SEARCHING = "SEARCHING";
    public static final String RIDE_STATUS_BIDDING = "BIDDING";
    public static final String RIDE_STATUS_ACCEPTED = "ACCEPTED";
    public static final String RIDE_STATUS_DRIVER_ARRIVING = "DRIVER_ARRIVING";
    public static final String RIDE_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String RIDE_STATUS_COMPLETED = "COMPLETED";
    public static final String RIDE_STATUS_CANCELLED = "CANCELLED";

    // Ride Messages
    public static final String MSG_RIDE_CREATED = "Ride request created successfully";
    public static final String MSG_RIDE_STARTED = "Ride has started";
    public static final String MSG_RIDE_COMPLETED = "Ride completed successfully";
    public static final String MSG_RIDE_CANCELLED = "Ride cancelled";

    // ─── BID STATUS ──────────────────────────────────────────────────────────
    public static final String BID_STATUS_PENDING = "PENDING";
    public static final String BID_STATUS_ACCEPTED = "ACCEPTED";
    public static final String BID_STATUS_REJECTED = "REJECTED";
    public static final String BID_STATUS_WITHDRAWN = "WITHDRAWN";
    public static final String BID_STATUS_EXPIRED = "EXPIRED";

    // Bid Messages
    public static final String MSG_BID_PLACED = "Bid placed successfully";
    public static final String MSG_BID_ACCEPTED = "Bid accepted successfully";
    public static final String MSG_BID_REJECTED = "Bid rejected";
    public static final String MSG_BID_WITHDRAWN = "Bid withdrawn successfully";

    // ─── COMMISSION ──────────────────────────────────────────────────────────
    public static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("15.00");
    public static final String COMMISSION_LEVEL_DEFAULT = "DEFAULT";
    public static final String COMMISSION_LEVEL_COUNTRY = "COUNTRY";
    public static final String COMMISSION_LEVEL_CITY = "CITY";
    public static final String COMMISSION_LEVEL_ZONE = "ZONE";

    // ─── WALLET ──────────────────────────────────────────────────────────────
    public static final String WALLET_TX_EARNING = "EARNING";
    public static final String WALLET_TX_COMMISSION = "COMMISSION";
    public static final String WALLET_TX_WITHDRAWAL = "WITHDRAWAL";
    public static final String WALLET_TX_TOP_UP = "TOP_UP";
    public static final String WALLET_TX_REFUND = "REFUND";

    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 86400000L;

    private Constants() {
    }
}
