import api from '../../../core/api/apiClient';

const verificationService = {
  // Submit verification request
  submitVerificationRequest: async (data) => {
    const response = await api.post('/verification/submit', data);
    return response.data;
  },

  // Get user verification status
  getVerificationStatus: async () => {
    const response = await api.get('/verification/status');
    return response.data;
  },

  // Admin: Get all pending requests
  getAllPendingRequests: async () => {
    const response = await api.get('/verification/requests');
    return response.data;
  },

  // Admin: Approve verification
  approveVerification: async (requestId, data) => {
    const response = await api.put(`/verification/${requestId}/approve`, data);
    return response.data;
  },

  // Admin: Reject verification
  rejectVerification: async (requestId, data) => {
    const response = await api.put(`/verification/${requestId}/reject`, data);
    return response.data;
  },

  // Check if user is verified
  checkUserVerified: async (userId) => {
    const response = await api.get(`/verification/check/${userId}`);
    return response.data;
  }
};

export default verificationService;
