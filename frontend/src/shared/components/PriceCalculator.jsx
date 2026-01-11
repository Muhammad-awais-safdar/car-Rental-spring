import { useState, useEffect } from "react";

export default function PriceCalculator({
  startDate,
  endDate,
  dailyRate,
  weeklyRate,
  monthlyRate,
  deposit,
  selectedDriver,
  onCalculate,
}) {
  const [totalCost, setTotalCost] = useState(null);
  const [breakdown, setBreakdown] = useState(null);

  useEffect(() => {
    if (startDate && endDate) {
      calculatePrice();
    }
  }, [startDate, endDate, selectedDriver]); // Added selectedDriver to dependencies

  const calculatePrice = () => {
    const start = new Date(startDate);
    const end = new Date(endDate);

    if (end < start) {
      setTotalCost(null);
      setBreakdown(null);
      return;
    }

    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;

    let rentalCost = 0;
    let details = {};

    // Calculate based on duration
    if (days >= 30 && monthlyRate) {
      const months = Math.floor(days / 30);
      const remainingDays = days % 30;
      rentalCost = monthlyRate * months + dailyRate * remainingDays;
      details = {
        months,
        remainingDays,
        monthlyRate,
        dailyRate,
      };
    } else if (days >= 7 && weeklyRate) {
      const weeks = Math.floor(days / 7);
      const remainingDays = days % 7;
      rentalCost = weeklyRate * weeks + dailyRate * remainingDays;
      details = {
        weeks,
        remainingDays,
        weeklyRate,
        dailyRate,
      };
    } else {
      rentalCost = dailyRate * days;
      details = {
        days,
        dailyRate,
      };
    }

    const total = rentalCost + (deposit || 0);

    // Add driver costs if driver is selected
    let driverCost = 0;
    let driverAllowance = 0;
    if (selectedDriver) {
      driverCost = selectedDriver.dailyRate * days;
      // Food and other allowance: $20 per day
      driverAllowance = 20 * days;
    }

    const grandTotal = total + driverCost + driverAllowance;

    setTotalCost(grandTotal);
    setBreakdown({
      ...details,
      rentalCost,
      deposit: deposit || 0,
      days,
      driverCost,
      driverAllowance,
      driverName: selectedDriver?.name,
    });

    if (onCalculate) {
      onCalculate({
        total: grandTotal,
        rentalCost,
        deposit: deposit || 0,
        days,
        driverCost,
        driverAllowance,
      });
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">
        Price Calculator
      </h3>

      <div className="space-y-4">
        {/* Date Display */}
        {startDate && endDate && (
          <div className="bg-gray-50 rounded-lg p-4 border-2 border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-gray-500 font-semibold mb-1">
                  Start Date
                </p>
                <p className="text-sm font-bold text-gray-900">
                  {formatDate(startDate)}
                </p>
              </div>
              <svg
                className="w-5 h-5 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M14 5l7 7m0 0l-7 7m7-7H3"
                />
              </svg>
              <div className="text-right">
                <p className="text-xs text-gray-500 font-semibold mb-1">
                  End Date
                </p>
                <p className="text-sm font-bold text-gray-900">
                  {formatDate(endDate)}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Breakdown */}
        {breakdown && (
          <div className="border-t pt-4 space-y-3">
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Duration:</span>
              <span className="font-medium text-gray-900">
                {breakdown.days} days
              </span>
            </div>

            {breakdown.months > 0 && (
              <>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">
                    {breakdown.months} month(s):
                  </span>
                  <span className="font-medium text-gray-900">
                    ${(breakdown.monthlyRate * breakdown.months).toFixed(2)}
                  </span>
                </div>
                {breakdown.remainingDays > 0 && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">
                      {breakdown.remainingDays} day(s):
                    </span>
                    <span className="font-medium text-gray-900">
                      $
                      {(breakdown.dailyRate * breakdown.remainingDays).toFixed(
                        2
                      )}
                    </span>
                  </div>
                )}
              </>
            )}

            {breakdown.weeks > 0 && !breakdown.months && (
              <>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">
                    {breakdown.weeks} week(s):
                  </span>
                  <span className="font-medium text-gray-900">
                    ${(breakdown.weeklyRate * breakdown.weeks).toFixed(2)}
                  </span>
                </div>
                {breakdown.remainingDays > 0 && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">
                      {breakdown.remainingDays} day(s):
                    </span>
                    <span className="font-medium text-gray-900">
                      $
                      {(breakdown.dailyRate * breakdown.remainingDays).toFixed(
                        2
                      )}
                    </span>
                  </div>
                )}
              </>
            )}

            {!breakdown.months && !breakdown.weeks && (
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">{breakdown.days} day(s):</span>
                <span className="font-medium text-gray-900">
                  ${breakdown.rentalCost.toFixed(2)}
                </span>
              </div>
            )}

            <div className="flex justify-between text-sm border-t pt-3">
              <span className="text-gray-600">Rental Cost:</span>
              <span className="font-semibold text-gray-900">
                ${breakdown.rentalCost.toFixed(2)}
              </span>
            </div>

            {breakdown.deposit > 0 && (
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Security Deposit:</span>
                <span className="font-semibold text-gray-900">
                  ${breakdown.deposit.toFixed(2)}
                </span>
              </div>
            )}

            {/* Driver Costs */}
            {breakdown.driverCost > 0 && (
              <>
                <div className="border-t pt-3 mt-3">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-bold text-gray-700">
                      Driver: {breakdown.driverName}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">
                      Driver Cost ({breakdown.days} days):
                    </span>
                    <span className="font-semibold text-gray-900">
                      ${breakdown.driverCost.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm mt-1">
                    <span className="text-gray-600">
                      Food & Allowance ({breakdown.days} days × $20):
                    </span>
                    <span className="font-semibold text-gray-900">
                      ${breakdown.driverAllowance.toFixed(2)}
                    </span>
                  </div>
                </div>
              </>
            )}

            <div className="flex justify-between text-lg font-bold border-t pt-3">
              <span className="text-gray-900">Total:</span>
              <span className="text-[#EF4444]">${totalCost.toFixed(2)}</span>
            </div>
          </div>
        )}

        {!breakdown && startDate && endDate && (
          <p className="text-sm text-gray-500 text-center py-4">
            Select valid dates to see price breakdown
          </p>
        )}
      </div>
    </div>
  );
}
