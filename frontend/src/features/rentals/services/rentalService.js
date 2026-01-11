import api from '../../../core/api/apiClient';

export const rentalService = {
    createRental: async (data) => {
        const response = await api.post('/rentals', data);
        return response.data;
    },

    updateRental: async (id, data) => {
        const response = await api.put(`/rentals/${id}`, data);
        return response.data;
    },

    deleteRental: async (id) => {
        const response = await api.delete(`/rentals/${id}`);
        return response.data;
    },

    getRentalById: async (id) => {
        const response = await api.get(`/rentals/${id}`);
        return response.data;
    },

    getRentalByListingId: async (listingId) => {
        const response = await api.get(`/rentals/listing/${listingId}`);
        return response.data;
    },

    getUserRentals: async () => {
        const response = await api.get('/rentals/my');
        return response.data;
    },

    checkAvailability: async (id, dates) => {
        const response = await api.post(`/rentals/${id}/check-availability`, dates);
        return response.data;
    },
};
