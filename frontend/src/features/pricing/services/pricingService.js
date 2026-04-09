import api from '../../../core/api/apiClient';

const pricingService = {
  // Estimate vehicle price
  estimatePrice: async (data) => {
    const response = await api.post('/pricing/estimate', data);
    return response.data;
  },

  // Get similar vehicle prices
  getSimilarVehiclePrices: async (listingId) => {
    const response = await api.get(`/pricing/similar/${listingId}`);
    return response.data;
  },

  // Get market trends
  getMarketTrends: async (make, model, year) => {
    const response = await api.get('/pricing/trends', {
      params: { make, model, year }
    });
    return response.data;
  }
};

export default pricingService;
