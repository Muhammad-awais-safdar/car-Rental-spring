import api from '../../../core/api/apiClient';

export const bookingService = {
    createBooking: async (data) => {
        const response = await api.post('/bookings', data);
        return response.data;
    },

    confirmBooking: async (id) => {
        const response = await api.put(`/bookings/${id}/confirm`);
        return response.data;
    },

    cancelBooking: async (id) => {
        const response = await api.put(`/bookings/${id}/cancel`);
        return response.data;
    },

    getBookingById: async (id) => {
        const response = await api.get(`/bookings/${id}`);
        return response.data;
    },

    getUserBookings: async (params = {}) => {
        const { page = 0, size = 10, sortBy = 'createdAt', sortOrder = 'desc' } = params;
        const response = await api.get('/bookings/my', {
            params: { page, size, sortBy, sortOrder },
        });
        return response.data;
    },

    getUserBookingsByStatus: async (status, params = {}) => {
        const { page = 0, size = 10 } = params;
        const response = await api.get(`/bookings/my/status/${status}`, {
            params: { page, size },
        });
        return response.data;
    },

    getRentalBookings: async (rentalId) => {
        const response = await api.get(`/bookings/rental/${rentalId}`);
        return response.data;
    },
};
