import React from "react";
import { Shield, TrendingUp, Clock, Star } from "lucide-react";
import VerifiedBadge from "./VerifiedBadge";

const TrustIndicators = ({ user, showAll = true }) => {
  const indicators = [
    {
      icon: Shield,
      label: "Verification",
      value: user.verificationLevel || "NONE",
      component: (
        <VerifiedBadge verificationLevel={user.verificationLevel} size="sm" />
      ),
    },
    {
      icon: TrendingUp,
      label: "Successful Deals",
      value: user.successfulTransactions || 0,
      show: showAll,
    },
    {
      icon: Clock,
      label: "Response Time",
      value: user.averageResponseTime || "< 1 hour",
      show: showAll,
    },
    {
      icon: Star,
      label: "Rating",
      value: `${user.rating || 0}/5.0`,
      show: showAll,
    },
  ];

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <h3 className="text-lg font-semibold mb-4">Trust Indicators</h3>
      <div className="space-y-3">
        {indicators
          .filter((ind) => ind.show !== false)
          .map((indicator, index) => {
            const Icon = indicator.icon;
            return (
              <div key={index} className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Icon className="w-5 h-5 text-gray-400" />
                  <span className="text-sm text-gray-600">
                    {indicator.label}
                  </span>
                </div>
                <div className="text-sm font-medium">
                  {indicator.component || indicator.value}
                </div>
              </div>
            );
          })}
      </div>

      {user.yearsOnPlatform && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <p className="text-sm text-gray-600">
            Member since {new Date().getFullYear() - user.yearsOnPlatform}
          </p>
        </div>
      )}
    </div>
  );
};

export default TrustIndicators;
