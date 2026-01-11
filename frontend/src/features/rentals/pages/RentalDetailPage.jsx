import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { rentalService } from "../services/rentalService";
import { bookingService } from "../../bookings/services/bookingService";
import { listingService } from "../../listings/services/listingService";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";
import ImageGallery from "../../../shared/components/ImageGallery";
import AvailabilityCalendar from "../../../shared/components/AvailabilityCalendar";
import PriceCalculator from "../../../shared/components/PriceCalculator";
import { useAuth } from "../../auth/context/AuthContext";
import API_ENDPOINTS from "../../../api/endpoints";
import api from "../../../core/api/api";

export default function RentalDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  console.log(isAuthenticated);
  const [rental, setRental] = useState(null);
  const [listing, setListing] = useState(null);
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [bookingData, setBookingData] = useState({
    startDate: "",
    endDate: "",
    driverName: "",
    driverEmail: "",
    driverPhone: "",
    driverLicense: "",
    needsDriver: false,
    driverId: null,
    pickupLocation: "",
    dropoffLocation: "",
    notes: "",
  });
  const [availability, setAvailability] = useState(null);
  const [checkingAvailability, setCheckingAvailability] = useState(false);
  const [showDriverList, setShowDriverList] = useState(false);
  useEffect(() => {
    fetchRentalDetails();
    fetchAvailableDrivers();
  }, [slug]);

  const fetchRentalDetails = async () => {
    try {
      // First get the listing by slug
      const listingResponse = await listingService.getListingBySlug(slug);
      setListing(listingResponse.data);

      // Then get the rental using the listing ID
      const rentalResponse = await rentalService.getRentalByListingId(
        listingResponse.data.id
      );
      setRental(rentalResponse.data);
    } catch (error) {
      console.error("Error fetching rental:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableDrivers = async () => {
    try {
      // Fetch available drivers from API using authenticated client
      const response = await api.get("/drivers");
      if (response.data) {
        setDrivers(response.data || []);
      }
    } catch (error) {
      console.warn(
        "Unable to fetch drivers:",
        error.statusCode || error.message
      );
      // Silently fail - drivers are optional
    }
  };

  const handleDateChange = (dates) => {
    setBookingData((prev) => ({
      ...prev,
      startDate: dates.startDate,
      endDate: dates.endDate,
    }));
  };

  // Helper function to format date as YYYY-MM-DD
  const formatDateForAPI = (date) => {
    if (!date) return null;
    if (typeof date === "string") return date;
    // If it's a Date object, format it
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const handleCheckAvailability = async () => {
    if (!bookingData.startDate || !bookingData.endDate) {
      alert("Please select both start and end dates");
      return;
    }

    setCheckingAvailability(true);
    try {
      const response = await rentalService.checkAvailability(rental.id, {
        startDate: formatDateForAPI(bookingData.startDate),
        endDate: formatDateForAPI(bookingData.endDate),
      });
      setAvailability(response.data);
    } catch (error) {
      alert("Failed to check availability");
    } finally {
      setCheckingAvailability(false);
    }
  };

  const handleSelectDriver = (driver) => {
    setBookingData((prev) => ({
      ...prev,
      driverId: driver.id,
      needsDriver: true,
    }));
    setShowDriverList(false);
  };

  const handleBookNow = async () => {
    // Validate required fields
    if (!bookingData.startDate || !bookingData.endDate) {
      alert("Please select both start and end dates");
      return;
    }

    if (!bookingData.pickupLocation || !bookingData.dropoffLocation) {
      alert("Please enter pickup and dropoff locations");
      return;
    }

    try {
      const response = await bookingService.createBooking({
        rentalId: rental.id,
        ...bookingData,
        startDate: formatDateForAPI(bookingData.startDate),
        endDate: formatDateForAPI(bookingData.endDate),
      });
      // Store booking data in localStorage for confirmation page
      localStorage.setItem("lastBooking", JSON.stringify(response.data));
      navigate("/bookings/confirmation");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to create booking");
    }
  };

  if (loading) return <LoadingSpinner />;
  if (!rental || !listing)
    return <div className="text-center py-16">Rental not found</div>;

  const selectedDriver = drivers.find((d) => d.id === bookingData.driverId);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="mb-6 flex items-center text-gray-600 hover:text-[#DC2626] transition-colors font-semibold"
        >
          <svg
            className="w-5 h-5 mr-2"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 19l-7-7 7-7"
            />
          </svg>
          Back to Rentals
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Column - Listing Details */}
          <div className="lg:col-span-2 space-y-6">
            {/* Image Gallery */}
            <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
              <ImageGallery images={listing.images || []} />
            </div>

            {/* Vehicle Info Card */}
            <div className="bg-white rounded-2xl shadow-xl p-8">
              <div className="flex items-start justify-between mb-6">
                <div>
                  <h1 className="text-4xl font-black text-gray-900 mb-2">
                    {listing.title}
                  </h1>
                  <div className="flex items-center gap-3">
                    <span className="px-4 py-1.5 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white rounded-full text-sm font-bold">
                      For Rent
                    </span>
                    <span className="text-gray-500">•</span>
                    <span className="text-gray-600 font-medium">
                      {listing.location}
                    </span>
                  </div>
                </div>
              </div>

              <p className="text-gray-700 text-lg leading-relaxed mb-8">
                {listing.description}
              </p>

              {/* Specifications Grid */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                <div className="text-center p-4 bg-gray-50 rounded-xl">
                  <div className="text-3xl mb-2">🚗</div>
                  <p className="text-gray-500 text-sm mb-1">Make & Model</p>
                  <p className="font-bold text-gray-900">
                    {listing.make} {listing.model}
                  </p>
                </div>
                <div className="text-center p-4 bg-gray-50 rounded-xl">
                  <div className="text-3xl mb-2">📅</div>
                  <p className="text-gray-500 text-sm mb-1">Year</p>
                  <p className="font-bold text-gray-900">{listing.year}</p>
                </div>
                <div className="text-center p-4 bg-gray-50 rounded-xl">
                  <div className="text-3xl mb-2">⚙️</div>
                  <p className="text-gray-500 text-sm mb-1">Transmission</p>
                  <p className="font-bold text-gray-900">
                    {listing.transmission || "Automatic"}
                  </p>
                </div>
                <div className="text-center p-4 bg-gray-50 rounded-xl">
                  <div className="text-3xl mb-2">⛽</div>
                  <p className="text-gray-500 text-sm mb-1">Fuel Type</p>
                  <p className="font-bold text-gray-900">
                    {listing.fuelType || "Gasoline"}
                  </p>
                </div>
              </div>
            </div>

            {/* Rental Rates Card */}
            <div className="bg-white rounded-2xl shadow-xl p-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">
                Rental Rates
              </h2>
              <div className="space-y-4">
                <div className="flex justify-between items-center p-4 bg-gradient-to-r from-red-50 to-orange-50 rounded-xl border-2 border-[#DC2626]">
                  <span className="text-gray-700 font-semibold">
                    Daily Rate
                  </span>
                  <span className="text-3xl font-black bg-gradient-to-r from-[#DC2626] to-[#B91C1C] bg-clip-text text-transparent">
                    ${rental.dailyRate}
                  </span>
                </div>
                {rental.weeklyRate && (
                  <div className="flex justify-between items-center p-4 bg-gray-50 rounded-xl">
                    <span className="text-gray-700 font-semibold">
                      Weekly Rate
                    </span>
                    <span className="text-2xl font-bold text-gray-900">
                      ${rental.weeklyRate}
                    </span>
                  </div>
                )}
                {rental.monthlyRate && (
                  <div className="flex justify-between items-center p-4 bg-gray-50 rounded-xl">
                    <span className="text-gray-700 font-semibold">
                      Monthly Rate
                    </span>
                    <span className="text-2xl font-bold text-gray-900">
                      ${rental.monthlyRate}
                    </span>
                  </div>
                )}
                {rental.deposit && (
                  <div className="flex justify-between items-center p-4 bg-yellow-50 rounded-xl border border-yellow-200">
                    <span className="text-gray-700 font-semibold">
                      Security Deposit
                    </span>
                    <span className="text-xl font-bold text-yellow-700">
                      ${rental.deposit}
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Right Column - Booking Form */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-2xl p-6 sticky top-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">
                Book This Vehicle
              </h2>

              {!isAuthenticated() ? (
                /* Login Required Message */
                <div className="text-center py-12">
                  <div className="mb-6">
                    <svg
                      className="w-20 h-20 mx-auto text-gray-300"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                      />
                    </svg>
                  </div>
                  <h3 className="text-xl font-bold text-gray-900 mb-3">
                    Login Required
                  </h3>
                  <p className="text-gray-600 mb-8">
                    Please login to book this vehicle
                  </p>
                  <button
                    onClick={() => navigate("/login")}
                    className="w-full py-4 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200"
                  >
                    Login to Book
                  </button>
                  <p className="text-sm text-gray-500 mt-4">
                    Don't have an account?{" "}
                    <button
                      onClick={() => navigate("/register")}
                      className="text-[#DC2626] font-semibold hover:underline"
                    >
                      Sign up here
                    </button>
                  </p>
                </div>
              ) : (
                /* Booking Form - Only visible when authenticated */
                <div className="space-y-6">
                  {/* Calendar */}
                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-3">
                      Select Dates
                    </label>
                    <AvailabilityCalendar
                      onDateSelect={handleDateChange}
                      blockedDates={[]}
                    />

                    {/* Check Availability Button */}
                    {bookingData.startDate && bookingData.endDate && (
                      <div className="mt-4 space-y-3">
                        <button
                          onClick={handleCheckAvailability}
                          disabled={checkingAvailability}
                          className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          {checkingAvailability
                            ? "Checking..."
                            : "Check Availability"}
                        </button>

                        {/* Availability Status */}
                        {availability && (
                          <div
                            className={`p-4 rounded-xl border-2 ${
                              availability.available
                                ? "bg-green-50 border-green-500"
                                : "bg-red-50 border-red-500"
                            }`}
                          >
                            <div className="flex items-center space-x-2">
                              {availability.available ? (
                                <>
                                  <svg
                                    className="w-6 h-6 text-green-600"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                  >
                                    <path
                                      strokeLinecap="round"
                                      strokeLinejoin="round"
                                      strokeWidth="2"
                                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                                    />
                                  </svg>
                                  <span className="font-bold text-green-800">
                                    Available for booking!
                                  </span>
                                </>
                              ) : (
                                <>
                                  <svg
                                    className="w-6 h-6 text-red-600"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                  >
                                    <path
                                      strokeLinecap="round"
                                      strokeLinejoin="round"
                                      strokeWidth="2"
                                      d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
                                    />
                                  </svg>
                                  <span className="font-bold text-red-800">
                                    Not available for selected dates
                                  </span>
                                </>
                              )}
                            </div>
                            {availability.message && (
                              <p className="text-sm mt-2 text-gray-700">
                                {availability.message}
                              </p>
                            )}
                          </div>
                        )}
                      </div>
                    )}
                  </div>

                  {/* Price Calculator */}
                  {bookingData.startDate && bookingData.endDate && (
                    <PriceCalculator
                      startDate={bookingData.startDate}
                      endDate={bookingData.endDate}
                      dailyRate={rental.dailyRate}
                      weeklyRate={rental.weeklyRate}
                      monthlyRate={rental.monthlyRate}
                      deposit={rental.deposit}
                      selectedDriver={selectedDriver}
                    />
                  )}

                  {/* Driver Selection */}
                  <div>
                    <label className="flex items-center space-x-2 mb-3">
                      <input
                        type="checkbox"
                        checked={bookingData.needsDriver}
                        onChange={(e) =>
                          setBookingData((prev) => ({
                            ...prev,
                            needsDriver: e.target.checked,
                          }))
                        }
                        className="w-5 h-5 text-[#DC2626] border-gray-300 rounded focus:ring-[#DC2626]"
                      />
                      <span className="text-sm font-bold text-gray-700">
                        Need a Driver?
                      </span>
                    </label>

                    {bookingData.needsDriver && (
                      <div className="space-y-3">
                        <button
                          onClick={() => setShowDriverList(!showDriverList)}
                          className="w-full px-4 py-3 bg-gray-100 hover:bg-gray-200 rounded-xl font-semibold text-gray-700 transition-colors"
                        >
                          {selectedDriver
                            ? `Selected: ${selectedDriver.name}`
                            : "Choose from Available Drivers"}
                        </button>

                        {showDriverList && drivers.length > 0 && (
                          <div className="max-h-64 overflow-y-auto space-y-2 border border-gray-200 rounded-xl p-3">
                            {drivers.map((driver) => (
                              <div
                                key={driver.id}
                                onClick={() => handleSelectDriver(driver)}
                                className="p-3 bg-gray-50 hover:bg-[#DC2626] hover:text-white rounded-lg cursor-pointer transition-all group"
                              >
                                <div className="flex justify-between items-center">
                                  <div>
                                    <p className="font-bold">{driver.name}</p>
                                    <p className="text-sm opacity-75">
                                      {driver.yearsExperience} years exp • ⭐{" "}
                                      {driver.rating}
                                    </p>
                                  </div>
                                  <p className="font-bold">
                                    ${driver.dailyRate}/day
                                  </p>
                                </div>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    )}
                  </div>

                  {/* Location Fields */}
                  <div className="space-y-3">
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Pickup Location
                      </label>
                      <input
                        type="text"
                        value={bookingData.pickupLocation}
                        onChange={(e) =>
                          setBookingData((prev) => ({
                            ...prev,
                            pickupLocation: e.target.value,
                          }))
                        }
                        className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                        placeholder="Enter pickup address"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Dropoff Location
                      </label>
                      <input
                        type="text"
                        value={bookingData.dropoffLocation}
                        onChange={(e) =>
                          setBookingData((prev) => ({
                            ...prev,
                            dropoffLocation: e.target.value,
                          }))
                        }
                        className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                        placeholder="Enter dropoff address"
                      />
                    </div>
                  </div>

                  {/* Notes */}
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Additional Notes
                    </label>
                    <textarea
                      value={bookingData.notes}
                      onChange={(e) =>
                        setBookingData((prev) => ({
                          ...prev,
                          notes: e.target.value,
                        }))
                      }
                      rows="3"
                      className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none resize-none"
                      placeholder="Any special requirements?"
                    />
                  </div>

                  {/* Book Button */}
                  <button
                    onClick={handleBookNow}
                    disabled={!bookingData.startDate || !bookingData.endDate}
                    className="w-full py-4 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                  >
                    Book Now
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
