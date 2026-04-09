import api from '../../../core/api/apiClient';

const analyticsService = {
  // Track event
  trackEvent: async (data) => {
    const response = await api.post('/analytics/track', data);
    return response.data;
  },

  // Get listing analytics
  getListingAnalytics: async (listingId, start, end) => {
    const params = {};
    if (start) params.start = start;
    if (end) params.end = end;
    
    const response = await api.get(`/analytics/listing/${listingId}`, { params });
    return response.data;
  },

  // Get seller analytics
  getSellerAnalytics: async (start, end) => {
    const params = {};
    if (start) params.start = start;
    if (end) params.end = end;
    
    const response = await api.get('/analytics/seller', { params });
    return response.data;
  },

  // Get platform analytics (admin)
  getPlatformAnalytics: async (start, end) => {
    const params = {};
    if (start) params.start = start;
    if (end) params.end = end;
    
    const response = await api.get('/analytics/platform', { params });
    return response.data;
  },

  // Get popular listings
  getPopularListings: async (start, end) => {
    const params = {};
    if (start) params.start = start;
    if (end) params.end = end;
    
    const response = await api.get('/analytics/popular', { params });
    return response.data;
  }
};

export default analyticsService;
