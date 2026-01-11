import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function RecentlyViewedSection() {
  const navigate = useNavigate();
  const [recentListings, setRecentListings] = useState([]);

  useEffect(() => {
    loadRecentlyViewed();
  }, []);

  const loadRecentlyViewed = () => {
    try {
      const recent = JSON.parse(localStorage.getItem("recentlyViewed") || "[]");
      setRecentListings(recent.slice(0, 4)); // Show last 4
    } catch (error) {
      console.error("Error loading recently viewed:", error);
    }
  };

  if (recentListings.length === 0) {
    return null;
  }

  return (
    <div className="bg-white rounded-2xl shadow-lg p-6 mt-8">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Recently Viewed</h2>
        <button
          onClick={() => {
            localStorage.removeItem("recentlyViewed");
            setRecentListings([]);
          }}
          className="text-sm text-gray-600 hover:text-[#DC2626] font-semibold"
        >
          Clear History
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {recentListings.map((listing) => (
          <div
            key={listing.id}
            onClick={() => navigate(`/listings/${listing.id}`)}
            className="cursor-pointer group"
          >
            <div className="bg-gray-100 rounded-xl overflow-hidden hover:shadow-lg transition-all">
              {/* Image */}
              <div className="h-40 bg-gray-200 relative overflow-hidden">
                {listing.image ? (
                  <img
                    src={listing.image}
                    alt={listing.title}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <svg
                      className="w-12 h-12 text-gray-400"
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

              {/* Content */}
              <div className="p-3">
                <h3 className="font-bold text-gray-900 text-sm truncate">
                  {listing.title}
                </h3>
                <p className="text-lg font-bold text-[#DC2626] mt-1">
                  ${listing.price?.toLocaleString()}
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Viewed {new Date(listing.viewedAt).toLocaleDateString()}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Helper function to add listing to recently viewed (call this from ListingDetailPage)
export const addToRecentlyViewed = (listing) => {
  try {
    const recent = JSON.parse(localStorage.getItem("recentlyViewed") || "[]");

    // Remove if already exists
    const filtered = recent.filter((item) => item.id !== listing.id);

    // Add to beginning
    const updated = [
      {
        id: listing.id,
        title: listing.title,
        price: listing.price,
        image: listing.images?.[0]?.imageUrl || null,
        viewedAt: new Date().toISOString(),
      },
      ...filtered,
    ].slice(0, 10); // Keep last 10

    localStorage.setItem("recentlyViewed", JSON.stringify(updated));
  } catch (error) {
    console.error("Error saving to recently viewed:", error);
  }
};
