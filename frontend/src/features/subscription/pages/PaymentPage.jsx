import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { paymentService, couponService } from "../services/subscriptionService";

export default function PaymentPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const plan = location.state?.plan || "BASIC";

  const [paymentMethod, setPaymentMethod] = useState("CARD");
  const [couponCode, setCouponCode] = useState("");
  const [discount, setDiscount] = useState(0);
  const [processing, setProcessing] = useState(false);

  const planPrices = {
    FREE: 0,
    BASIC: 9.99,
    PRO: 29.99,
    PREMIUM: 99.99,
  };

  const amount = planPrices[plan] || 0;
  const finalAmount = Math.max(0, amount - discount);

  const handleApplyCoupon = async () => {
    if (!couponCode.trim()) {
      alert("Please enter a coupon code");
      return;
    }

    try {
      const response = await couponService.validateCoupon(couponCode, amount);
      if (response.data.valid) {
        setDiscount(parseFloat(response.data.discountAmount));
        alert("Coupon applied successfully!");
      } else {
        alert(response.data.message);
      }
    } catch (error) {
      alert("Failed to validate coupon");
    }
  };

  const handlePayment = async () => {
    setProcessing(true);

    try {
      // Create payment
      const paymentResponse = await paymentService.createPayment(
        finalAmount,
        "SUBSCRIPTION",
        paymentMethod,
        JSON.stringify({ plan, couponCode })
      );

      // Simulate payment confirmation
      await paymentService.confirmPayment(
        paymentResponse.data.id,
        paymentResponse.data.transactionId
      );

      // Apply coupon if used
      if (couponCode) {
        await couponService.applyCoupon(couponCode);
      }

      alert("Payment successful! Your subscription is now active.");
      navigate("/subscription/plans");
    } catch (error) {
      alert(error.response?.data?.statusMessage || "Payment failed");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">
          Complete Your Payment
        </h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Payment Form */}
          <div className="lg:col-span-2 bg-white rounded-xl shadow-lg p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-6">
              Payment Method
            </h2>

            <div className="space-y-4 mb-6">
              <label className="flex items-center p-4 border-2 border-gray-200 rounded-xl cursor-pointer hover:border-[#DC2626] transition-colors">
                <input
                  type="radio"
                  name="paymentMethod"
                  value="CARD"
                  checked={paymentMethod === "CARD"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-4 h-4 text-[#DC2626]"
                />
                <span className="ml-3 font-semibold">Credit/Debit Card</span>
              </label>

              <label className="flex items-center p-4 border-2 border-gray-200 rounded-xl cursor-pointer hover:border-[#DC2626] transition-colors">
                <input
                  type="radio"
                  name="paymentMethod"
                  value="PAYPAL"
                  checked={paymentMethod === "PAYPAL"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-4 h-4 text-[#DC2626]"
                />
                <span className="ml-3 font-semibold">PayPal</span>
              </label>

              <label className="flex items-center p-4 border-2 border-gray-200 rounded-xl cursor-pointer hover:border-[#DC2626] transition-colors">
                <input
                  type="radio"
                  name="paymentMethod"
                  value="BANK_TRANSFER"
                  checked={paymentMethod === "BANK_TRANSFER"}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-4 h-4 text-[#DC2626]"
                />
                <span className="ml-3 font-semibold">Bank Transfer</span>
              </label>
            </div>

            {paymentMethod === "CARD" && (
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Card Number
                  </label>
                  <input
                    type="text"
                    placeholder="1234 5678 9012 3456"
                    className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      Expiry Date
                    </label>
                    <input
                      type="text"
                      placeholder="MM/YY"
                      className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">
                      CVV
                    </label>
                    <input
                      type="text"
                      placeholder="123"
                      className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                    />
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Order Summary */}
          <div className="bg-white rounded-xl shadow-lg p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-6">
              Order Summary
            </h2>

            <div className="space-y-4 mb-6">
              <div className="flex justify-between">
                <span className="text-gray-600">Plan</span>
                <span className="font-semibold">{plan}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Subtotal</span>
                <span className="font-semibold">${amount.toFixed(2)}</span>
              </div>
              {discount > 0 && (
                <div className="flex justify-between text-green-600">
                  <span>Discount</span>
                  <span className="font-semibold">-${discount.toFixed(2)}</span>
                </div>
              )}
              <div className="border-t pt-4 flex justify-between text-lg">
                <span className="font-bold">Total</span>
                <span className="font-bold text-[#DC2626]">
                  ${finalAmount.toFixed(2)}
                </span>
              </div>
            </div>

            {/* Coupon Code */}
            <div className="mb-6">
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Coupon Code
              </label>
              <div className="flex space-x-2">
                <input
                  type="text"
                  value={couponCode}
                  onChange={(e) => setCouponCode(e.target.value)}
                  placeholder="Enter code"
                  className="flex-1 px-4 py-2 border-2 border-gray-200 rounded-xl focus:border-[#DC2626] focus:ring-4 focus:ring-red-100 transition-all outline-none"
                />
                <button
                  onClick={handleApplyCoupon}
                  className="px-4 py-2 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-colors"
                >
                  Apply
                </button>
              </div>
            </div>

            <button
              onClick={handlePayment}
              disabled={processing}
              className="w-full px-6 py-4 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white rounded-xl font-bold text-lg hover:from-[#B91C1C] hover:to-[#991B1B] transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {processing ? "Processing..." : `Pay $${finalAmount.toFixed(2)}`}
            </button>

            <p className="text-xs text-gray-500 text-center mt-4">
              Secure payment powered by our platform
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
