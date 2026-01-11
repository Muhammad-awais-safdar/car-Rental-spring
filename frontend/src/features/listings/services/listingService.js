import api from '../../../core/api/api';


export const listingService = {
    // Create new listing
    createListing: async (data) => {
        return await api.post('/listings', data);
    },

    // Update listing
    updateListing: async (id, data) => {
        return await api.put(`/listings/${id}`, data);
    },

    // Delete listing
    deleteListing: async (id) => {
        return await api.delete(`/listings/${id}`);
    },

    // Get listing by ID
    getListingById: async (id) => {
        return await api.get(`/listings/${id}`);
    },

    // Get listing by slug
    getListingBySlug: async (slug) => {
        return await api.get(`/listings/${slug}`);
    },

    // Search listings
    searchListings: async (params) => {
        return await api.get('/listings/search', { params });
    },

    // Get user's listings
    getMyListings: async () => {
        return await api.get('/listings/my');
    },
};
