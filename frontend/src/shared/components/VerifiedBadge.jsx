import React from "react";
import { CheckCircle, Shield, Star } from "lucide-react";

const VerifiedBadge = ({
  verificationLevel = "NONE",
  size = "md",
  showLabel = true,
}) => {
  if (verificationLevel === "NONE") return null;

  const sizeClasses = {
    sm: "w-4 h-4",
    md: "w-6 h-6",
    lg: "w-8 h-8",
  };

  const badgeConfig = {
    BASIC: {
      icon: CheckCircle,
      color: "text-blue-500",
      bgColor: "bg-blue-50",
      label: "Verified",
      description: "Basic verification completed",
    },
    VERIFIED: {
      icon: Shield,
      color: "text-green-500",
      bgColor: "bg-green-50",
      label: "Verified Seller",
      description: "Identity and documents verified",
    },
    PREMIUM: {
      icon: Star,
      color: "text-yellow-500",
      bgColor: "bg-yellow-50",
      label: "Premium Verified",
      description: "Premium verified seller with excellent track record",
    },
  };

  const config = badgeConfig[verificationLevel] || badgeConfig.BASIC;
  const Icon = config.icon;

  return (
    <div className="inline-flex items-center gap-2 group relative">
      <div
        className={`flex items-center gap-1.5 px-2 py-1 rounded-full ${config.bgColor}`}
      >
        <Icon className={`${sizeClasses[size]} ${config.color}`} />
        {showLabel && (
          <span className={`text-sm font-medium ${config.color}`}>
            {config.label}
          </span>
        )}
      </div>

      {/* Tooltip */}
      <div className="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-sm rounded-lg opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap z-10">
        {config.description}
        <div className="absolute top-full left-1/2 transform -translate-x-1/2 -mt-1 border-4 border-transparent border-t-gray-900"></div>
      </div>
    </div>
  );
};

export default VerifiedBadge;
