import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { listingService } from "../services/listingService";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";

export default function CompareListingsPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const ids = searchParams.get("ids")?.split(",").filter(Boolean) || [];
    if (ids.length === 0) {
      navigate("/listings");
      return;
    }
    fetchListings(ids);
  }, [searchParams]);

  const fetchListings = async (ids) => {
    try {
      const promises = ids.map((id) => listingService.getListingById(id));
      const responses = await Promise.all(promises);
      setListings(responses.map((r) => r.data));
    } catch (error) {
      console.error("Error fetching listings:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = (listingId) => {
    const ids =
      searchParams
        .get("ids")
        ?.split(",")
        .filter((id) => id !== listingId.toString()) || [];
    if (ids.length === 0) {
      navigate("/listings");
    } else {
      navigate(`/compare?ids=${ids.join(",")}`);
    }
  };

  if (loading) return <LoadingSpinner />;

  const features = [
    { key: "make", label: "Make" },
    { key: "model", label: "Model" },
    { key: "year", label: "Year" },
    {
      key: "price",
      label: "Price",
      format: (val) => `$${val?.toLocaleString()}`,
    },
    {
      key: "mileage",
      label: "Mileage",
      format: (val) => `${val?.toLocaleString()} km`,
    },
    { key: "fuelType", label: "Fuel Type" },
    { key: "transmission", label: "Transmission" },
    { key: "color", label: "Color" },
    { key: "location", label: "Location" },
    { key: "condition", label: "Condition" },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <button
            onClick={() => navigate("/listings")}
            className="text-[#DC2626] hover:underline font-semibold mb-4 flex items-center"
          >
            <svg
              className="w-5 h-5 mr-1"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M15 19l-7-7 7-7"
              />
            </svg>
            Back to Listings
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Compare Vehicles</h1>
          <p className="text-gray-600 mt-2">
            Comparing {listings.length}{" "}
            {listings.length === 1 ? "vehicle" : "vehicles"}
          </p>
        </div>

        {/* Comparison Table */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-50 border-b">
                  <th className="px-6 py-4 text-left text-sm font-bold text-gray-900 w-48">
                    Feature
                  </th>
                  {listings.map((listing) => (
                    <th
                      key={listing.id}
                      className="px-6 py-4 text-center min-w-64"
                    >
                      <div className="relative">
                        {/* Remove Button */}
                        <button
                          onClick={() => handleRemove(listing.id)}
                          className="absolute top-0 right-0 p-1 text-red-600 hover:bg-red-50 rounded-full transition-colors"
                          title="Remove from comparison"
                        >
                          <svg
                            className="w-5 h-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth="2"
                              d="M6 18L18 6M6 6l12 12"
                            />
                          </svg>
                        </button>

                        {/* Image */}
                        <div className="h-40 bg-gray-200 rounded-lg mb-3 overflow-hidden">
                          {listing.images && listing.images.length > 0 ? (
                            <img
                              src={listing.images[0].imageUrl}
                              alt={listing.title}
                              className="w-full h-full object-cover"
                            />
                          ) : (
                            <div className="w-full h-full flex items-center justify-center">
                              <svg
                                className="w-16 h-16 text-gray-400"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                              >
                                <path
                                  strokeLinecap="round"
                                  strokeLinejoin="round"
                                  strokeWidth="2"
                                  d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                                />
                              </svg>
                            </div>
                          )}
                        </div>

                        {/* Title */}
                        <h3 className="font-bold text-gray-900 text-sm mb-2">
                          {listing.title}
                        </h3>

                        {/* View Button */}
                        <button
                          onClick={() => navigate(`/listings/${listing.id}`)}
                          className="w-full py-2 bg-[#DC2626] text-white rounded-lg text-sm font-semibold hover:bg-[#B91C1C] transition-colors"
                        >
                          View Details
                        </button>
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {features.map((feature, index) => (
                  <tr
                    key={feature.key}
                    className={index % 2 === 0 ? "bg-white" : "bg-gray-50"}
                  >
                    <td className="px-6 py-4 text-sm font-semibold text-gray-700">
                      {feature.label}
                    </td>
                    {listings.map((listing) => (
                      <td
                        key={listing.id}
                        className="px-6 py-4 text-center text-sm text-gray-900"
                      >
                        {feature.format
                          ? feature.format(listing[feature.key])
                          : listing[feature.key] || "N/A"}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Add More Button */}
        {listings.length < 3 && (
          <div className="mt-6 text-center">
            <button
              onClick={() => navigate("/listings")}
              className="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors"
            >
              + Add More Vehicles to Compare
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
