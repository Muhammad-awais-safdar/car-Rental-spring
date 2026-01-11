import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../core/api/apiClient";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export default function PromotionBanner({ position = "HOMEPAGE" }) {
  const navigate = useNavigate();
  const [banners, setBanners] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBanners();
  }, [position]);

  useEffect(() => {
    if (banners.length > 1) {
      const interval = setInterval(() => {
        setCurrentIndex((prev) => (prev + 1) % banners.length);
      }, 5000); // Auto-rotate every 5 seconds

      return () => clearInterval(interval);
    }
  }, [banners.length]);

  const fetchBanners = async () => {
    try {
      const response = await api.get(
        `${API_BASE}/promotions/banners/position/${position}`
      );
      setBanners(response.data.data || []);
    } catch (error) {
      console.error("Error fetching banners:", error);
      setBanners([]);
    } finally {
      setLoading(false);
    }
  };

  const handleBannerClick = (banner) => {
    if (banner.linkUrl) {
      if (banner.linkUrl.startsWith("http")) {
        window.open(banner.linkUrl, "_blank");
      } else {
        navigate(banner.linkUrl);
      }
    }
  };

  if (loading || banners.length === 0) {
    return null;
  }

  const currentBanner = banners[currentIndex];

  return (
    <div className="relative w-full overflow-hidden rounded-2xl shadow-2xl mb-8">
      {/* Banner Content */}
      <div
        className="relative h-64 md:h-80 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] cursor-pointer"
        onClick={() => handleBannerClick(currentBanner)}
        style={{
          backgroundImage: currentBanner.imageUrl
            ? `url(${currentBanner.imageUrl})`
            : "none",
          backgroundSize: "cover",
          backgroundPosition: "center",
        }}
      >
        {/* Overlay */}
        <div className="absolute inset-0 bg-gradient-to-r from-black/60 to-black/30"></div>

        {/* Content */}
        <div className="relative h-full flex items-center px-8 md:px-16">
          <div className="max-w-2xl">
            <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">
              {currentBanner.title}
            </h2>
            {currentBanner.description && (
              <p className="text-xl text-gray-100 mb-6">
                {currentBanner.description}
              </p>
            )}
            {currentBanner.buttonText && (
              <button className="px-8 py-4 bg-white text-[#DC2626] rounded-xl font-bold text-lg hover:bg-gray-100 transition-all transform hover:scale-105 shadow-lg">
                {currentBanner.buttonText}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Navigation Dots */}
      {banners.length > 1 && (
        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2">
          {banners.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentIndex(index)}
              className={`w-3 h-3 rounded-full transition-all ${
                index === currentIndex
                  ? "bg-white w-8"
                  : "bg-white/50 hover:bg-white/75"
              }`}
              aria-label={`Go to banner ${index + 1}`}
            />
          ))}
        </div>
      )}

      {/* Navigation Arrows */}
      {banners.length > 1 && (
        <>
          <button
            onClick={() =>
              setCurrentIndex(
                (prev) => (prev - 1 + banners.length) % banners.length
              )
            }
            className="absolute left-4 top-1/2 transform -translate-y-1/2 p-3 bg-white/20 hover:bg-white/40 rounded-full backdrop-blur-sm transition-all"
            aria-label="Previous banner"
          >
            <svg
              className="w-6 h-6 text-white"
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
          </button>
          <button
            onClick={() =>
              setCurrentIndex((prev) => (prev + 1) % banners.length)
            }
            className="absolute right-4 top-1/2 transform -translate-y-1/2 p-3 bg-white/20 hover:bg-white/40 rounded-full backdrop-blur-sm transition-all"
            aria-label="Next banner"
          >
            <svg
              className="w-6 h-6 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M9 5l7 7-7 7"
              />
            </svg>
          </button>
        </>
      )}
    </div>
  );
}
