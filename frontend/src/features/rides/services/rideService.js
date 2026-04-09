import api from '../../../core/api/apiClient';

export const rideService = {

    // ─── PASSENGER ACTIONS ───────────────────────────────────────────────────

    /**
     * Create a new ride request
     * @param {Object} data - { pickupAddress, pickupLat, pickupLng, dropoffAddress, dropoffLat, dropoffLng, offeredPrice?, vehicleType?, notes?, estimatedDistanceKm?, estimatedDurationMin? }
     */
    createRide: async (data) => {
        const response = await api.post('/rides', data);
        return response.data;
    },

    /**
     * Passenger accepts a driver's bid
     */
    acceptBid: async (rideId, bidId) => {
        const response = await api.put(`/rides/${rideId}/accept-bid/${bidId}`);
        return response.data;
    },

    /**
     * Passenger cancels their ride
     */
    cancelRideByPassenger: async (rideId, reason) => {
        const response = await api.put(`/rides/${rideId}/cancel`, { reason });
        return response.data;
    },

    // ─── DRIVER ACTIONS ──────────────────────────────────────────────────────

    /**
     * Driver marks themselves as arrived at pickup
     */
    markDriverArrived: async (rideId) => {
        const response = await api.put(`/rides/${rideId}/driver-arrived`);
        return response.data;
    },

    /**
     * Driver starts the ride
     */
    startRide: async (rideId) => {
        const response = await api.put(`/rides/${rideId}/start`);
        return response.data;
    },

    /**
     * Driver completes the ride
     */
    completeRide: async (rideId) => {
        const response = await api.put(`/rides/${rideId}/complete`);
        return response.data;
    },

    /**
     * Driver cancels the ride
     */
    cancelRideByDriver: async (rideId, reason) => {
        const response = await api.put(`/rides/${rideId}/driver-cancel`, { reason });
        return response.data;
    },

    // ─── QUERIES ─────────────────────────────────────────────────────────────

    /**
     * Get ride details by ID
     */
    getRideById: async (rideId) => {
        const response = await api.get(`/rides/${rideId}`);
        return response.data;
    },

    /**
     * Get passenger's ride history
     */
    getMyRidesAsPassenger: async (page = 0, size = 10) => {
        const response = await api.get('/rides/my/passenger', { params: { page, size } });
        return response.data;
    },

    /**
     * Get driver's ride history
     */
    getMyRidesAsDriver: async (page = 0, size = 10) => {
        const response = await api.get('/rides/my/driver', { params: { page, size } });
        return response.data;
    },

    /**
     * Get nearby available ride requests (for drivers)
     */
    getNearbyRides: async (lat, lng) => {
        const response = await api.get('/rides/nearby', { params: { lat, lng } });
        return response.data;
    },

    // ─── BID ACTIONS ─────────────────────────────────────────────────────────

    /**
     * Driver places a bid on a ride
     * @param {number} rideId
     * @param {Object} data - { bidAmount, message?, estimatedArrivalMin? }
     */
    placeBid: async (rideId, data) => {
        const response = await api.post(`/rides/${rideId}/bids`, data);
        return response.data;
    },

    /**
     * Driver withdraws their bid
     */
    withdrawBid: async (bidId) => {
        const response = await api.delete(`/rides/bids/${bidId}`);
        return response.data;
    },

    /**
     * Passenger views all bids on their ride
     */
    getBidsForRide: async (rideId) => {
        const response = await api.get(`/rides/${rideId}/bids`);
        return response.data;
    },

    /**
     * Driver views all their bids
     */
    getMyBids: async () => {
        const response = await api.get('/rides/bids/my');
        return response.data;
    },
};
