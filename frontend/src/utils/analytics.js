import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

// Google Analytics tracking ID
const GA_TRACKING_ID = import.meta.env.VITE_GA_TRACKING_ID || 'G-XXXXXXXXXX';

// Initialize Google Analytics
export const initGA = () => {
    if (typeof window !== 'undefined' && !window.gtag) {
        const script = document.createElement('script');
        script.async = true;
        script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_TRACKING_ID}`;
        document.head.appendChild(script);

        window.dataLayer = window.dataLayer || [];
        window.gtag = function() {
            window.dataLayer.push(arguments);
        };
        window.gtag('js', new Date());
        window.gtag('config', GA_TRACKING_ID, {
            page_path: window.location.pathname,
        });
    }
};

// Track page views
export const trackPageView = (url) => {
    if (typeof window !== 'undefined' && window.gtag) {
        window.gtag('config', GA_TRACKING_ID, {
            page_path: url,
        });
    }
};

// Track events
export const trackEvent = (action, category, label, value) => {
    if (typeof window !== 'undefined' && window.gtag) {
        window.gtag('event', action, {
            event_category: category,
            event_label: label,
            value: value,
        });
    }
};

// Predefined event trackers
export const analytics = {
    // User events
    trackSignUp: (method) => {
        trackEvent('sign_up', 'User', method);
    },

    trackLogin: (method) => {
        trackEvent('login', 'User', method);
    },

    // Listing events
    trackListingView: (listingId, listingTitle) => {
        trackEvent('view_item', 'Listing', listingTitle, listingId);
    },

    trackListingCreate: (listingId) => {
        trackEvent('create_listing', 'Listing', 'New Listing', listingId);
    },

    trackListingShare: (listingId, platform) => {
        trackEvent('share', 'Listing', platform, listingId);
    },

    // Booking events
    trackBookingStart: (listingId) => {
        trackEvent('begin_checkout', 'Booking', 'Start Booking', listingId);
    },

    trackBookingComplete: (bookingId, amount) => {
        trackEvent('purchase', 'Booking', 'Complete Booking', amount);
    },

    // Subscription events
    trackSubscriptionView: () => {
        trackEvent('view_item_list', 'Subscription', 'View Plans');
    },

    trackSubscriptionSelect: (plan) => {
        trackEvent('select_item', 'Subscription', plan);
    },

    trackSubscriptionPurchase: (plan, amount) => {
        trackEvent('purchase', 'Subscription', plan, amount);
    },

    // Search events
    trackSearch: (query) => {
        trackEvent('search', 'Search', query);
    },

    trackFilterApply: (filterType, filterValue) => {
        trackEvent('filter', 'Search', `${filterType}: ${filterValue}`);
    },

    // Wishlist events
    trackAddToWishlist: (listingId) => {
        trackEvent('add_to_wishlist', 'Wishlist', 'Add Item', listingId);
    },

    trackRemoveFromWishlist: (listingId) => {
        trackEvent('remove_from_wishlist', 'Wishlist', 'Remove Item', listingId);
    },

    // Review events
    trackReviewSubmit: (listingId, rating) => {
        trackEvent('submit_review', 'Review', 'Rating', rating);
    },

    // Message events
    trackMessageSend: () => {
        trackEvent('send_message', 'Messaging', 'New Message');
    },

    // Payment events
    trackPaymentStart: (amount, type) => {
        trackEvent('begin_checkout', 'Payment', type, amount);
    },

    trackPaymentComplete: (amount, type) => {
        trackEvent('purchase', 'Payment', type, amount);
    },

    trackCouponApply: (couponCode, discount) => {
        trackEvent('apply_coupon', 'Payment', couponCode, discount);
    }
};

// React Hook for tracking page views
export const usePageTracking = () => {
    const location = useLocation();

    useEffect(() => {
        trackPageView(location.pathname + location.search);
    }, [location]);
};

export default analytics;
