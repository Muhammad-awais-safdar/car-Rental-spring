import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { subscriptionService } from "../services/subscriptionService";
import LoadingSpinner from "../../../shared/components/LoadingSpinner";

export default function SubscriptionPlansPage() {
  const navigate = useNavigate();
  const [plans, setPlans] = useState([]);
  const [currentPlan, setCurrentPlan] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPlans();
    fetchCurrentSubscription();
  }, []);

  const fetchPlans = async () => {
    try {
      const response = await subscriptionService.getAllPlans();
      setPlans(response.data);
    } catch (error) {
      console.error("Error fetching plans:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchCurrentSubscription = async () => {
    try {
      const response = await subscriptionService.getCurrentSubscription();
      setCurrentPlan(response.data.plan);
    } catch (error) {
      // User might not have a subscription
      setCurrentPlan(null);
    }
  };

  const handleSubscribe = async (planName) => {
    try {
      await subscriptionService.subscribe(planName);
      alert("Subscription successful! Redirecting to payment...");
      navigate("/payment", { state: { plan: planName } });
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Failed to subscribe");
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Choose Your Plan
          </h1>
          <p className="text-xl text-gray-600">
            Select the perfect plan for your car rental business
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {plans.map((plan) => (
            <div
              key={plan.name}
              className={`bg-white rounded-2xl shadow-xl overflow-hidden transform transition-all hover:scale-105 ${
                currentPlan === plan.name ? "ring-4 ring-[#DC2626]" : ""
              }`}
            >
              <div
                className={`p-8 ${
                  plan.name === "PREMIUM"
                    ? "bg-gradient-to-br from-[#DC2626] to-[#B91C1C] text-white"
                    : ""
                }`}
              >
                <h3
                  className={`text-2xl font-bold mb-2 ${
                    plan.name === "PREMIUM" ? "text-white" : "text-gray-900"
                  }`}
                >
                  {plan.displayName}
                </h3>
                <div className="flex items-baseline mb-4">
                  <span
                    className={`text-5xl font-extrabold ${
                      plan.name === "PREMIUM" ? "text-white" : "text-gray-900"
                    }`}
                  >
                    ${plan.price}
                  </span>
                  <span
                    className={`ml-2 ${
                      plan.name === "PREMIUM"
                        ? "text-gray-100"
                        : "text-gray-600"
                    }`}
                  >
                    /month
                  </span>
                </div>
                <p
                  className={`mb-6 ${
                    plan.name === "PREMIUM" ? "text-gray-100" : "text-gray-600"
                  }`}
                >
                  {plan.description}
                </p>
              </div>

              <div className="p-8">
                <ul className="space-y-4 mb-8">
                  <li className="flex items-center">
                    <svg
                      className="w-5 h-5 text-green-500 mr-3"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fillRule="evenodd"
                        d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                        clipRule="evenodd"
                      />
                    </svg>
                    <span className="text-gray-700">
                      {plan.listingLimit === 999
                        ? "Unlimited"
                        : plan.listingLimit}{" "}
                      Listings
                    </span>
                  </li>
                  {plan.featured && (
                    <li className="flex items-center">
                      <svg
                        className="w-5 h-5 text-green-500 mr-3"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                      <span className="text-gray-700">Featured Badge</span>
                    </li>
                  )}
                  {plan.prioritySupport && (
                    <li className="flex items-center">
                      <svg
                        className="w-5 h-5 text-green-500 mr-3"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                      <span className="text-gray-700">Priority Support</span>
                    </li>
                  )}
                  <li className="flex items-center">
                    <svg
                      className="w-5 h-5 text-green-500 mr-3"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fillRule="evenodd"
                        d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                        clipRule="evenodd"
                      />
                    </svg>
                    <span className="text-gray-700">Analytics Dashboard</span>
                  </li>
                </ul>

                {currentPlan === plan.name ? (
                  <button
                    disabled
                    className="w-full px-6 py-3 bg-gray-300 text-gray-600 rounded-xl font-semibold cursor-not-allowed"
                  >
                    Current Plan
                  </button>
                ) : (
                  <button
                    onClick={() => handleSubscribe(plan.name)}
                    className={`w-full px-6 py-3 rounded-xl font-semibold transition-colors ${
                      plan.name === "PREMIUM"
                        ? "bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white hover:from-[#B91C1C] hover:to-[#991B1B]"
                        : "bg-[#DC2626] text-white hover:bg-[#B91C1C]"
                    }`}
                  >
                    Subscribe Now
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

        {currentPlan && (
          <div className="mt-12 text-center">
            <p className="text-gray-600 mb-4">
              Current Plan:{" "}
              <span className="font-bold text-[#DC2626]">{currentPlan}</span>
            </p>
            <button
              onClick={() => navigate("/subscription/history")}
              className="text-[#DC2626] hover:text-[#B91C1C] font-semibold"
            >
              View Subscription History →
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
