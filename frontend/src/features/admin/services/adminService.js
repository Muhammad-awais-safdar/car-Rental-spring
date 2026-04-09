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

// Category Management Service
export const categoryService = {
    getAllMakes: async () => {
        const response = await api.get('/categories/makes');
        return response;
    },

    getMakeWithModels: async (makeId) => {
        const response = await api.get(`/categories/makes/${makeId}`);
        return response;
    },

    createMake: async (name, logoUrl) => {
        const response = await api.post('/categories/makes', { name, logoUrl });
        return response;
    },

    createModel: async (makeId, name) => {
        const response = await api.post(`/categories/makes/${makeId}/models`, { name });
        return response;
    },

    deleteMake: async (makeId) => {
        const response = await api.delete(`/categories/makes/${makeId}`);
        return response;
    },

    deleteModel: async (modelId) => {
        const response = await api.delete(`/categories/models/${modelId}`);
        return response;
    },
};

// Analytics Service for Admin
export const analyticsService = {
    getOverviewStats: async () => {
        const response = await api.get('/admin/analytics/overview');
        return response;
    },

    getUserGrowthStats: async () => {
        const response = await api.get('/admin/analytics/user-growth');
        return response;
    },

    getListingStats: async () => {
        const response = await api.get('/admin/analytics/listings');
        return response;
    },

    getBookingStats: async () => {
        const response = await api.get('/admin/analytics/bookings');
        return response;
    },

    getRevenueStats: async () => {
        const response = await api.get('/admin/analytics/revenue');
        return response;
    },
};

// Admin User Management Service
export const adminUserService = {
    getAllUsers: async (page = 0, size = 20) => {
        const response = await api.get('/admin/users', {
            params: { page, size },
        });
        return response;
    },

    getUserDetails: async (userId) => {
        const response = await api.get(`/admin/users/${userId}`);
        return response;
    },

    getUserStatistics: async () => {
        const response = await api.get('/admin/users/stats');
        return response;
    },

    blockUser: async (userId, reason) => {
        const response = await api.put(`/admin/users/${userId}/block`, { reason });
        return response;
    },

    unblockUser: async (userId) => {
        const response = await api.put(`/admin/users/${userId}/unblock`);
        return response;
    },

    updateUserRole: async (userId, role) => {
        const response = await api.put(`/admin/users/${userId}/role`, { role });
        return response;
    },
};
