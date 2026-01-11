import api from '../../../core/api/apiClient';

export const adminService = {
    // Listing Management
    getPendingListings: async (params = {}) => {
        const { page = 0, size = 10 } = params;
        const response = await api.get('/admin/listings/pending', {
            params: { page, size },
        });
        return response.data;
    },

    approveListing: async (id) => {
        const response = await api.put(`/admin/listings/${id}/approve`);
        return response.data;
    },

    rejectListing: async (id) => {
        const response = await api.put(`/admin/listings/${id}/reject`);
        return response.data;
    },

    // Booking Management
    confirmBooking: async (id) => {
        const response = await api.put(`/bookings/${id}/confirm`);
        return response.data;
    },
};
