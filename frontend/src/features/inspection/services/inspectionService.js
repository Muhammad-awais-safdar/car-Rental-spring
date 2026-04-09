import api from '../../../core/api/apiClient';

const inspectionService = {
  // Create inspection report
  createInspectionReport: async (data) => {
    const response = await api.post('/inspections', data);
    return response.data;
  },

  // Get listing inspection
  getListingInspection: async (listingId) => {
    const response = await api.get(`/inspections/listing/${listingId}`);
    return response.data;
  },

  // Get listing inspection history
  getListingInspectionHistory: async (listingId) => {
    const response = await api.get(`/inspections/listing/${listingId}/history`);
    return response.data;
  },

  // Delete inspection report
  deleteInspectionReport: async (id) => {
    const response = await api.delete(`/inspections/${id}`);
    return response.data;
  }
};

export default inspectionService;
