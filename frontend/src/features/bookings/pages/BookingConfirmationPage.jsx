import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function BookingConfirmationPage() {
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);

  useEffect(() => {
    // Get booking data from localStorage
    const lastBookingData = localStorage.getItem("lastBooking");
    if (lastBookingData) {
      setBooking(JSON.parse(lastBookingData));
      // Clear it after reading
      localStorage.removeItem("lastBooking");
    }
  }, []);

  if (!booking) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            Booking Not Found
          </h2>
          <p className="text-gray-600 mb-4">
            No booking information available.
          </p>
          <button onClick={() => navigate("/rentals")} className="btn-primary">
            Browse Vehicles
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Success Message */}
        <div className="bg-green-50 border-2 border-green-200 rounded-lg p-6 mb-8 text-center">
          <div className="flex justify-center mb-4">
            <div className="bg-green-100 p-3 rounded-full">
              <svg
                className="w-12 h-12 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
            </div>
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Booking Confirmed!
          </h1>
          <p className="text-gray-600">
            Your rental booking has been successfully created.
          </p>
        </div>

        {/* Booking Details Card */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Booking Details
          </h2>

          <div className="space-y-4">
            {/* Booking ID */}
            <div className="flex justify-between items-center pb-4 border-b">
              <span className="text-gray-600">Booking ID:</span>
              <span className="font-semibold text-gray-900">#{booking.id}</span>
            </div>

            {/* Car Details */}
            <div className="pb-4 border-b">
              <h3 className="text-sm font-medium text-gray-600 mb-2">
                Vehicle
              </h3>
              <p className="text-lg font-semibold text-gray-900">
                {booking.listingTitle}
              </p>
              {booking.listingImage && (
                <img
                  src={booking.listingImage}
                  alt={booking.listingTitle}
                  className="w-full h-48 object-cover rounded-lg mt-3"
                />
              )}
            </div>

            {/* Rental Period */}
            <div className="pb-4 border-b">
              <h3 className="text-sm font-medium text-gray-600 mb-2">
                Rental Period
              </h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-gray-500">Start Date</p>
                  <p className="font-semibold text-gray-900">
                    {booking.startDate}
                  </p>
                </div>
                <div>
                  <p className="text-xs text-gray-500">End Date</p>
                  <p className="font-semibold text-gray-900">
                    {booking.endDate}
                  </p>
                </div>
              </div>
            </div>

            {/* Payment Details */}
            <div className="pb-4 border-b">
              <h3 className="text-sm font-medium text-gray-600 mb-2">
                Payment Summary
              </h3>
              <div className="flex justify-between items-center">
                <span className="text-gray-900">Total Amount:</span>
                <span className="text-2xl font-bold text-[#EF4444]">
                  ${booking.totalAmount}
                </span>
              </div>
            </div>

            {/* Status */}
            <div>
              <h3 className="text-sm font-medium text-gray-600 mb-2">Status</h3>
              <span
                className={`inline-block px-4 py-2 rounded-full text-sm font-semibold ${
                  booking.status === "CONFIRMED"
                    ? "bg-green-100 text-green-800"
                    : "bg-yellow-100 text-yellow-800"
                }`}
              >
                {booking.status}
              </span>
            </div>
          </div>
        </div>

        {/* Next Steps */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3">
            What's Next?
          </h3>
          <ul className="space-y-2 text-gray-700">
            <li className="flex items-start">
              <svg
                className="w-5 h-5 text-blue-600 mr-2 mt-0.5"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
              <span>You will receive a confirmation email shortly</span>
            </li>
            <li className="flex items-start">
              <svg
                className="w-5 h-5 text-blue-600 mr-2 mt-0.5"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
              <span>The car owner will review your booking request</span>
            </li>
            <li className="flex items-start">
              <svg
                className="w-5 h-5 text-blue-600 mr-2 mt-0.5"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                  clipRule="evenodd"
                />
              </svg>
              <span>Check your bookings page for updates</span>
            </li>
          </ul>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-4">
          <button
            onClick={() => navigate("/bookings/my")}
            className="btn-primary flex-1"
          >
            View My Bookings
          </button>
          <button
            onClick={() => navigate("/rentals")}
            className="btn-outline flex-1"
          >
            Browse More Cars
          </button>
        </div>
      </div>
    </div>
  );
}
