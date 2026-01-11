import api from '../../../core/api/api';
import API_ENDPOINTS from '../../../api/endpoints';

export const wishlistService = {
    addToWishlist: async (listingId) => {
        return await api.post(API_ENDPOINTS.WISHLIST.BASE, { listingId });
    },

    removeFromWishlist: async (listingId) => {
        return await api.delete(`${API_ENDPOINTS.WISHLIST.BASE}/${listingId}`);
    },

    getUserWishlist: async () => {
        return await api.get(API_ENDPOINTS.WISHLIST.BASE);
    },

    checkWishlist: async (listingId) => {
        return await api.get(`${API_ENDPOINTS.WISHLIST.BASE}/check/${listingId}`);
    },

    getWishlistCount: async () => {
        return await api.get(`${API_ENDPOINTS.WISHLIST.BASE}/count`);
    }
};
