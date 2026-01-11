// API Base URL from environment variables
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// API Endpoints Configuration
const API_ENDPOINTS = {
  // Authentication
  auth: {
    register: `${API_BASE_URL}/auth/register`,
    login: `${API_BASE_URL}/auth/login`,
    logout: `${API_BASE_URL}/auth/logout`,
    profile: `${API_BASE_URL}/auth/profile`,
  },

  // Listings (Vehicles)
  listings: {
    base: `${API_BASE_URL}/listings`,
    search: `${API_BASE_URL}/listings/search`,
    bySlug: (slug) => `${API_BASE_URL}/listings/${slug}`,
    byId: (id) => `${API_BASE_URL}/listings/${id}`,
    myListings: `${API_BASE_URL}/listings/my`,
    userListings: (userId) => `${API_BASE_URL}/listings/user/${userId}`,
    create: `${API_BASE_URL}/listings`,
    update: (id) => `${API_BASE_URL}/listings/${id}`,
    delete: (id) => `${API_BASE_URL}/listings/${id}`,
    checkAvailability: (slug) => `${API_BASE_URL}/listings/${slug}/availability`,
  },

  // Rentals
  rentals: {
    base: `${API_BASE_URL}/rentals`,
    byId: (id) => `${API_BASE_URL}/rentals/${id}`,
    bySlug: (slug) => `${API_BASE_URL}/rentals/${slug}`,
    myRentals: `${API_BASE_URL}/rentals/my`,
  },

  // Bookings
  bookings: {
    base: `${API_BASE_URL}/bookings`,
    create: `${API_BASE_URL}/bookings`,
    byId: (id) => `${API_BASE_URL}/bookings/${id}`,
    myBookings: `${API_BASE_URL}/bookings/my`,
    cancel: (id) => `${API_BASE_URL}/bookings/${id}/cancel`,
    approve: (id) => `${API_BASE_URL}/bookings/${id}/approve`,
    reject: (id) => `${API_BASE_URL}/bookings/${id}/reject`,
    checkAvailability: `${API_BASE_URL}/bookings/check-availability`,
  },

  // Admin
  admin: {
    pendingListings: `${API_BASE_URL}/admin/listings/pending`,
    approveListing: (id) => `${API_BASE_URL}/admin/listings/${id}/approve`,
    rejectListing: (id) => `${API_BASE_URL}/admin/listings/${id}/reject`,
    users: `${API_BASE_URL}/admin/users`,
    stats: `${API_BASE_URL}/admin/stats`,
  },

  // Drivers
  drivers: {
    base: `${API_BASE_URL}/drivers`,
    byId: (id) => `${API_BASE_URL}/drivers/${id}`,
    myDrivers: `${API_BASE_URL}/drivers/my`,
    create: `${API_BASE_URL}/drivers`,
    update: (id) => `${API_BASE_URL}/drivers/${id}`,
    delete: (id) => `${API_BASE_URL}/drivers/${id}`,
  },

  // Wishlist
  WISHLIST: {
    BASE: `${API_BASE_URL}/wishlist`,
  },
};

// Export both the base URL and endpoints
export { API_BASE_URL, API_ENDPOINTS };
export default API_ENDPOINTS;
