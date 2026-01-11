import { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { listingService } from "../services/listingService";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";

export default function ListingDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    fetchListing();
  }, [slug]);

  const fetchListing = async () => {
    try {
      const response = await listingService.getListingBySlug(slug);
      setListing(response.data);
    } catch (error) {
      console.error("Error fetching listing:", error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;
  if (!listing)
    return <div className="text-center py-16">Listing not found</div>;

  const images = listing.images || [];
  const hasImages = images.length > 0;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="mb-6 flex items-center text-gray-600 hover:text-gray-900"
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
          Back
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Image Gallery */}
          <div>
            <div className="aspect-video bg-gray-200 rounded-lg overflow-hidden mb-4">
              {hasImages ? (
                <img
                  src={images[currentImageIndex]}
                  alt={listing.title}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-gray-400">
                  <svg
                    className="w-24 h-24"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                    />
                  </svg>
                </div>
              )}
            </div>

            {/* Thumbnails */}
            {hasImages && images.length > 1 && (
              <div className="grid grid-cols-4 gap-2">
                {images.map((image, index) => (
                  <button
                    key={index}
                    onClick={() => setCurrentImageIndex(index)}
                    className={`aspect-video rounded-lg overflow-hidden border-2 ${
                      currentImageIndex === index
                        ? "border-[#EF4444]"
                        : "border-transparent"
                    }`}
                  >
                    <img
                      src={image}
                      alt={`${listing.title} ${index + 1}`}
                      className="w-full h-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Details */}
          <div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <h1 className="text-3xl font-bold text-gray-900 mb-4">
                {listing.title}
              </h1>

              <div className="flex gap-2 mb-4">
                <span
                  className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
                    listing.listingType === "SELL"
                      ? "bg-blue-100 text-blue-800"
                      : "bg-purple-100 text-purple-800"
                  }`}
                >
                  {listing.listingType === "SELL" ? "For Sale" : "For Rent"}
                </span>
                {listing.status && (
                  <span
                    className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
                      listing.status === "APPROVED"
                        ? "bg-green-100 text-green-800"
                        : "bg-yellow-100 text-yellow-800"
                    }`}
                  >
                    {listing.status}
                  </span>
                )}
              </div>

              <div className="text-4xl font-bold text-[#EF4444] mb-6">
                ${listing.price?.toLocaleString()}
              </div>

              {/* Specifications */}
              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <p className="text-gray-600 text-sm">Make</p>
                  <p className="font-semibold text-gray-900">{listing.make}</p>
                </div>
                <div>
                  <p className="text-gray-600 text-sm">Model</p>
                  <p className="font-semibold text-gray-900">{listing.model}</p>
                </div>
                <div>
                  <p className="text-gray-600 text-sm">Year</p>
                  <p className="font-semibold text-gray-900">{listing.year}</p>
                </div>
                <div>
                  <p className="text-gray-600 text-sm">Mileage</p>
                  <p className="font-semibold text-gray-900">
                    {listing.mileage?.toLocaleString()} km
                  </p>
                </div>
                <div>
                  <p className="text-gray-600 text-sm">Location</p>
                  <p className="font-semibold text-gray-900">
                    {listing.location}
                  </p>
                </div>
                <div>
                  <p className="text-gray-600 text-sm">Condition</p>
                  <p className="font-semibold text-gray-900">
                    {listing.condition}
                  </p>
                </div>
              </div>

              {/* Description */}
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">
                  Description
                </h2>
                <p className="text-gray-700 whitespace-pre-line">
                  {listing.description}
                </p>
              </div>

              {/* Actions */}
              <div className="flex gap-4">
                {listing.listingType === "RENT" ? (
                  <Link
                    to={`/rentals/${slug}`}
                    className="btn-primary flex-1 text-center"
                  >
                    Book Now
                  </Link>
                ) : (
                  <button className="btn-primary flex-1">Contact Seller</button>
                )}
                <button className="btn-outline">
                  <svg
                    className="w-5 h-5"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                    />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
