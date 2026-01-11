import api from '../../../core/api/api';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const reviewService = {
    createReview: async (listingId, rating, comment) => {
        return await api.post(`${API_BASE}/reviews`, {
            listingId,
            rating,
            comment
        });
    },

    updateReview: async (reviewId, rating, comment) => {
        return await api.put(`${API_BASE}/reviews/${reviewId}`, {
            rating,
            comment
        });
    },

    deleteReview: async (reviewId) => {
        return await api.delete(`${API_BASE}/reviews/${reviewId}`);
    },

    getListingReviews: async (listingId, page = 0, size = 10) => {
        return await api.get(`${API_BASE}/reviews/listings/${listingId}?page=${page}&size=${size}`);
    },

    getUserReviews: async () => {
        return await api.get(`${API_BASE}/reviews/my`);
    },

    getListingStatistics: async (listingId) => {
        return await api.get(`${API_BASE}/reviews/listings/${listingId}/statistics`);
    }
};
