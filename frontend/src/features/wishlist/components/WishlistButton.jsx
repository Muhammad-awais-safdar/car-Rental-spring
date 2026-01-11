import { useState, useEffect } from "react";
import { wishlistService } from "../services/wishlistService";
import { useAuth } from "../../auth/context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function WishlistButton({ listingId, className = "" }) {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [inWishlist, setInWishlist] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isAuthenticated()) {
      checkWishlistStatus();
    }
  }, [listingId]);

  const checkWishlistStatus = async () => {
    try {
      const response = await wishlistService.checkWishlist(listingId);
      setInWishlist(response.data);
    } catch (error) {
      console.error("Error checking wishlist:", error);
    }
  };

  const handleToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();

    if (!isAuthenticated()) {
      navigate("/login");
      return;
    }

    setLoading(true);
    try {
      if (inWishlist) {
        await wishlistService.removeFromWishlist(listingId);
        setInWishlist(false);
      } else {
        await wishlistService.addToWishlist(listingId);
        setInWishlist(true);
      }
    } catch (error) {
      console.error("Error toggling wishlist:", error);
      alert(error.response?.data?.statusMessage || "Failed to update wishlist");
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      onClick={handleToggle}
      disabled={loading}
      className={`p-2 rounded-full transition-all ${
        inWishlist
          ? "bg-red-100 hover:bg-red-200"
          : "bg-white hover:bg-gray-100"
      } ${className}`}
      title={inWishlist ? "Remove from wishlist" : "Add to wishlist"}
    >
      <svg
        className={`w-6 h-6 ${inWishlist ? "text-red-600" : "text-gray-600"}`}
        fill={inWishlist ? "currentColor" : "none"}
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth="2"
          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
        />
      </svg>
    </button>
  );
}
