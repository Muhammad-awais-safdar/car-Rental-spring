import api from '../../../core/api/api';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const notificationService = {
    getUserNotifications: async (page = 0, size = 20) => {
        return await api.get(`${API_BASE}/notifications?page=${page}&size=${size}`);
    },

    getUnreadNotifications: async () => {
        return await api.get(`${API_BASE}/notifications/unread`);
    },

    getUnreadCount: async () => {
        return await api.get(`${API_BASE}/notifications/unread-count`);
    },

    markAsRead: async (notificationId) => {
        return await api.put(`${API_BASE}/notifications/${notificationId}/read`);
    },

    markAllAsRead: async () => {
        return await api.put(`${API_BASE}/notifications/read-all`);
    },

    deleteNotification: async (notificationId) => {
        return await api.delete(`${API_BASE}/notifications/${notificationId}`);
    }
};
