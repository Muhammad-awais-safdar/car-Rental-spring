import React from "react";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";

const MetricsCard = ({
  title,
  value,
  change,
  icon: Icon,
  trend = "neutral",
  format = "number",
}) => {
  const formatValue = (val) => {
    if (format === "currency") {
      return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
      }).format(val);
    } else if (format === "percentage") {
      return `${val.toFixed(2)}%`;
    }
    return new Intl.NumberFormat("en-US").format(val);
  };

  const getTrendIcon = () => {
    if (trend === "up") return <TrendingUp className="w-4 h-4" />;
    if (trend === "down") return <TrendingDown className="w-4 h-4" />;
    return <Minus className="w-4 h-4" />;
  };

  const getTrendColor = () => {
    if (trend === "up") return "text-green-600";
    if (trend === "down") return "text-red-600";
    return "text-gray-600";
  };

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-gray-600">{title}</h3>
        {Icon && (
          <div className="p-2 bg-blue-50 rounded-lg">
            <Icon className="w-6 h-6 text-blue-600" />
          </div>
        )}
      </div>

      <div className="flex items-baseline justify-between">
        <p className="text-3xl font-bold text-gray-900">{formatValue(value)}</p>

        {change !== undefined && (
          <div className={`flex items-center gap-1 ${getTrendColor()}`}>
            {getTrendIcon()}
            <span className="text-sm font-medium">
              {Math.abs(change).toFixed(1)}%
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default MetricsCard;
