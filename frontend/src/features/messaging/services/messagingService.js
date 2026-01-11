import api from '../../../core/api/api';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const messagingService = {
    sendMessage: async (receiverId, listingId, content) => {
        return await api.post(`${API_BASE}/messages`, {
            receiverId,
            listingId,
            content
        });
    },

    getUserConversations: async () => {
        return await api.get(`${API_BASE}/messages/conversations`);
    },

    getConversationMessages: async (conversationId) => {
        return await api.get(`${API_BASE}/messages/conversations/${conversationId}`);
    },

    getUnreadCount: async () => {
        return await api.get(`${API_BASE}/messages/unread-count`);
    }
};
