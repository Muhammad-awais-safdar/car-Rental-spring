import api from '../../../core/api/apiClient';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const subscriptionService = {
    getAllPlans: async () => {
        return await api.get(`${API_BASE}/subscriptions/plans`);
    },

    subscribe: async (plan) => {
        return await api.post(`${API_BASE}/subscriptions/subscribe`, { plan });
    },

    getCurrentSubscription: async () => {
        return await api.get(`${API_BASE}/subscriptions/current`);
    },

    cancelSubscription: async () => {
        return await api.put(`${API_BASE}/subscriptions/cancel`);
    },

    renewSubscription: async () => {
        return await api.put(`${API_BASE}/subscriptions/renew`);
    },

    getSubscriptionHistory: async () => {
        return await api.get(`${API_BASE}/subscriptions/history`);
    }
};

export const paymentService = {
    createPayment: async (amount, type, paymentMethod, metadata) => {
        return await api.post(`${API_BASE}/payments/create`, {
            amount,
            type,
            paymentMethod,
            metadata
        });
    },

    confirmPayment: async (paymentId, transactionId) => {
        return await api.post(`${API_BASE}/payments/${paymentId}/confirm`, { transactionId });
    },

    getPaymentHistory: async (page = 0, size = 20) => {
        return await api.get(`${API_BASE}/payments/history?page=${page}&size=${size}`);
    },

    getPaymentDetails: async (paymentId) => {
        return await api.get(`${API_BASE}/payments/${paymentId}`);
    }
};

export const couponService = {
    validateCoupon: async (code, amount) => {
        return await api.get(`${API_BASE}/coupons/validate/${code}?amount=${amount}`);
    },

    applyCoupon: async (code) => {
        return await api.post(`${API_BASE}/coupons/apply?code=${code}`);
    }
};
