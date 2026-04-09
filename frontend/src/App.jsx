import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { AuthProvider, useAuth } from "./features/auth/context/AuthContext";
import Navbar from "./shared/components/Navbar";

// Auth Pages
import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";

// Listing Pages
import HomePage from "./features/listings/pages/HomePage";
import ListingsPage from "./features/listings/pages/ListingsPage";
import ListingDetailPage from "./features/listings/pages/ListingDetailPage";
import CreateListingPage from "./features/listings/pages/CreateListingPage";
import EditListingPage from "./features/listings/pages/EditListingPage";
import MyListingsPage from "./features/listings/pages/MyListingsPage";
import CompareListingsPage from "./features/listings/pages/CompareListingsPage";

// Rental Pages
import RentalsPage from "./features/rentals/pages/RentalsPage";
import RentalDetailPage from "./features/rentals/pages/RentalDetailPage";

// Booking Pages
import MyBookingsPage from "./features/bookings/pages/MyBookingsPage";
import BookingConfirmationPage from "./features/bookings/pages/BookingConfirmationPage";

// Profile Pages
import ProfilePage from "./features/profile/pages/ProfilePage";
import ChangePasswordPage from "./features/profile/pages/ChangePasswordPage";

// Admin Pages
import AdminLayout from "./features/admin/components/AdminLayout";
import AdminDashboardPage from "./features/admin/pages/AdminDashboardPage";
import AdminUsersPage from "./features/admin/pages/AdminUsersPage";
import AdminReportsPage from "./features/admin/pages/AdminReportsPage";
import AdminCategoriesPage from "./features/admin/pages/AdminCategoriesPage";
import AdminActivityLogsPage from "./features/admin/pages/AdminActivityLogsPage";
import AdminPlaceholderPage from "./features/admin/pages/AdminPlaceholderPage";

// Wishlist Pages
import WishlistPage from "./features/wishlist/pages/WishlistPage";

// Messaging Pages
import MessagesPage from "./features/messaging/pages/MessagesPage";

// Subscription Pages
import SubscriptionPlansPage from "./features/subscription/pages/SubscriptionPlansPage";
import PaymentPage from "./features/subscription/pages/PaymentPage";

// Ride Pages (RideFlex)
import RequestRidePage from "./features/rides/pages/RequestRidePage";
import RideDetailPage from "./features/rides/pages/RideDetailPage";
import MyRidesPage from "./features/rides/pages/MyRidesPage";

import "./index.css";

// Protected Route Component
function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#EF4444]"></div>
      </div>
    );
  }

  return isAuthenticated() ? children : <Navigate to="/login" />;
}

// App Layout with Navbar
function AppLayout({ children }) {
  return (
    <>
      <Navbar />
      {children}
    </>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Public Routes with Navbar */}
          <Route
            path="/"
            element={
              <AppLayout>
                <HomePage />
              </AppLayout>
            }
          />
          <Route
            path="/listings"
            element={
              <AppLayout>
                <ListingsPage />
              </AppLayout>
            }
          />
          <Route
            path="/listings/:slug"
            element={
              <AppLayout>
                <ListingDetailPage />
              </AppLayout>
            }
          />
          <Route
            path="/rentals"
            element={
              <AppLayout>
                <RentalsPage />
              </AppLayout>
            }
          />
          <Route
            path="/rentals/:slug"
            element={
              <AppLayout>
                <RentalDetailPage />
              </AppLayout>
            }
          />

          {/* Protected Routes with Navbar */}
          <Route
            path="/listings/create"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <CreateListingPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/listings/:slug/edit"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <EditListingPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/my-listings"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <MyListingsPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/bookings/my"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <MyBookingsPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/bookings/confirmation"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <BookingConfirmationPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <ProfilePage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile/change-password"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <ChangePasswordPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          {/* Admin Routes with AdminLayout */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <AdminLayout />
              </ProtectedRoute>
            }
          >
            <Route path="dashboard" element={<AdminDashboardPage />} />
            <Route path="users" element={<AdminUsersPage />} />
            <Route path="users/:id" element={<AdminUsersPage />} />
            <Route path="reports" element={<AdminReportsPage />} />
            <Route path="categories" element={<AdminCategoriesPage />} />
            <Route path="activity-logs" element={<AdminActivityLogsPage />} />

            {/* Placeholder Routes */}
            <Route path="listings" element={<AdminPlaceholderPage />} />
            <Route path="bookings" element={<AdminPlaceholderPage />} />
            <Route path="reviews" element={<AdminPlaceholderPage />} />
            <Route path="analytics" element={<AdminPlaceholderPage />} />
            <Route path="settings" element={<AdminPlaceholderPage />} />
          </Route>

          {/* Wishlist Route */}
          <Route
            path="/wishlist"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <WishlistPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          {/* Messages Route */}
          <Route
            path="/messages"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <MessagesPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          {/* Subscription Routes */}
          <Route
            path="/subscription/plans"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <SubscriptionPlansPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/payment"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <PaymentPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          {/* Ride Routes (RideFlex) */}
          <Route
            path="/rides/request"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <RequestRidePage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/rides/my"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <MyRidesPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/rides/:id"
            element={
              <ProtectedRoute>
                <AppLayout>
                  <RideDetailPage />
                </AppLayout>
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
