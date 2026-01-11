export default function ListingCard({ listing, onClick }) {
  const getStatusBadge = (status) => {
    const badges = {
      APPROVED: "bg-gradient-to-r from-green-400 to-green-600 text-white",
      PENDING: "bg-gradient-to-r from-yellow-400 to-yellow-600 text-white",
      REJECTED: "bg-gradient-to-r from-red-400 to-red-600 text-white",
    };
    return badges[status] || "bg-gray-100 text-gray-800";
  };

  const getTypeBadge = (type) => {
    return type === "SELL"
      ? "bg-gradient-to-r from-blue-500 to-blue-700 text-white"
      : "bg-gradient-to-r from-purple-500 to-purple-700 text-white";
  };

  return (
    <div
      onClick={onClick}
      className="group bg-white rounded-2xl overflow-hidden cursor-pointer transform hover:-translate-y-2 transition-all duration-300 shadow-lg hover:shadow-2xl border border-gray-100"
    >
      {/* Image Container */}
      <div className="relative aspect-video bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden">
        {listing.images && listing.images.length > 0 ? (
          <img
            src={listing.images[0]}
            alt={listing.title}
            className="w-full h-full object-cover transform group-hover:scale-110 transition-transform duration-500"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <svg
              className="w-20 h-20 text-gray-300"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1.5}
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
          </div>
        )}

        {/* Overlay Gradient */}
        <div className="absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>

        {/* Badges */}
        <div className="absolute top-3 left-3 flex gap-2">
          <span
            className={`px-3 py-1.5 rounded-full text-xs font-bold shadow-lg ${getTypeBadge(
              listing.listingType
            )}`}
          >
            {listing.listingType === "SELL" ? "For Sale" : "For Rent"}
          </span>
        </div>

        {listing.status && (
          <div className="absolute top-3 right-3">
            <span
              className={`px-3 py-1.5 rounded-full text-xs font-bold shadow-lg ${getStatusBadge(
                listing.status
              )}`}
            >
              {listing.status}
            </span>
          </div>
        )}

        {/* Featured Badge */}
        {listing.isFeatured && (
          <div className="absolute bottom-3 right-3">
            <span className="px-3 py-1.5 bg-gradient-to-r from-yellow-400 to-yellow-600 text-white rounded-full text-xs font-bold shadow-lg flex items-center gap-1">
              <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
              </svg>
              Featured
            </span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-5">
        {/* Title */}
        <h3 className="text-xl font-bold text-gray-900 mb-2 truncate group-hover:text-[#DC2626] transition-colors">
          {listing.title}
        </h3>

        {/* Car Details */}
        <div className="flex items-center gap-2 text-sm text-gray-600 mb-3">
          <span className="flex items-center gap-1">
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
              />
            </svg>
            {listing.year}
          </span>
          <span>•</span>
          <span>{listing.make}</span>
          <span>•</span>
          <span>{listing.model}</span>
        </div>

        {/* Description */}
        <p className="text-gray-600 text-sm mb-4 line-clamp-2 leading-relaxed">
          {listing.description}
        </p>

        {/* Footer */}
        <div className="flex justify-between items-center pt-4 border-t border-gray-100">
          {/* Price */}
          <div>
            <p className="text-xs text-gray-500 mb-1">Price</p>
            <p className="text-2xl font-black bg-gradient-to-r from-[#DC2626] to-[#B91C1C] bg-clip-text text-transparent">
              ${listing.price?.toLocaleString()}
            </p>
          </div>

          {/* Location */}
          <div className="text-right">
            <p className="text-xs text-gray-500 mb-1">Location</p>
            <p className="text-sm font-semibold text-gray-700 flex items-center gap-1">
              <svg
                className="w-4 h-4 text-[#DC2626]"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                />
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
              {listing.location?.split(",")[0]}
            </p>
          </div>
        </div>

        {/* View Details Button */}
        <button className="mt-4 w-full py-3 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white rounded-xl font-semibold opacity-0 group-hover:opacity-100 transform translate-y-2 group-hover:translate-y-0 transition-all duration-300 shadow-lg hover:shadow-xl">
          View Details
        </button>
      </div>
    </div>
  );
}
