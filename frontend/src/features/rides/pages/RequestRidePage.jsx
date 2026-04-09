import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';

const VEHICLE_TYPES = ['ANY', 'CAR', 'BIKE', 'VAN'];

export default function RequestRidePage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [formData, setFormData] = useState({
        pickupAddress: '',
        pickupLat: '',
        pickupLng: '',
        dropoffAddress: '',
        dropoffLat: '',
        dropoffLng: '',
        offeredPrice: '',
        vehicleType: 'ANY',
        notes: '',
        estimatedDistanceKm: '',
        estimatedDurationMin: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const payload = {
                ...formData,
                pickupLat: formData.pickupLat ? parseFloat(formData.pickupLat) : null,
                pickupLng: formData.pickupLng ? parseFloat(formData.pickupLng) : null,
                dropoffLat: formData.dropoffLat ? parseFloat(formData.dropoffLat) : null,
                dropoffLng: formData.dropoffLng ? parseFloat(formData.dropoffLng) : null,
                offeredPrice: formData.offeredPrice ? parseFloat(formData.offeredPrice) : null,
                estimatedDistanceKm: formData.estimatedDistanceKm ? parseFloat(formData.estimatedDistanceKm) : null,
                estimatedDurationMin: formData.estimatedDurationMin ? parseInt(formData.estimatedDurationMin) : null,
            };

            const response = await rideService.createRide(payload);
            navigate(`/rides/${response.data.id}`);
        } catch (err) {
            setError(err.response?.data?.statusMessage || 'Failed to create ride request');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
            <div className="max-w-2xl mx-auto px-4 py-10">

                {/* Header */}
                <div className="mb-8">
                    <div className="inline-flex items-center gap-2 bg-amber-500/10 border border-amber-500/30 rounded-full px-4 py-1.5 mb-4">
                        <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
                        <span className="text-amber-400 text-sm font-medium">RideFlex</span>
                    </div>
                    <h1 className="text-4xl font-bold text-white mb-2">Request a Ride</h1>
                    <p className="text-slate-400 text-lg">Get competitive bids from nearby drivers</p>
                </div>

                {/* Form Card */}
                <div className="bg-slate-800/60 backdrop-blur-sm border border-slate-700/50 rounded-2xl p-8 shadow-2xl">
                    {error && (
                        <div className="bg-red-500/10 border border-red-500/30 rounded-xl p-4 mb-6 flex items-start gap-3">
                            <svg className="w-5 h-5 text-red-400 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <p className="text-red-400 text-sm">{error}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">

                        {/* Pickup */}
                        <div>
                            <h3 className="text-white font-semibold mb-4 flex items-center gap-2">
                                <span className="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center text-white text-xs font-bold">A</span>
                                Pickup Location
                            </h3>
                            <div className="space-y-3">
                                <input
                                    id="pickupAddress"
                                    name="pickupAddress"
                                    type="text"
                                    placeholder="Pickup address"
                                    value={formData.pickupAddress}
                                    onChange={handleChange}
                                    required
                                    className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                />
                                <div className="grid grid-cols-2 gap-3">
                                    <input
                                        id="pickupLat"
                                        name="pickupLat"
                                        type="number"
                                        step="any"
                                        placeholder="Latitude (e.g. 31.5204)"
                                        value={formData.pickupLat}
                                        onChange={handleChange}
                                        required
                                        className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all text-sm"
                                    />
                                    <input
                                        id="pickupLng"
                                        name="pickupLng"
                                        type="number"
                                        step="any"
                                        placeholder="Longitude (e.g. 74.3587)"
                                        value={formData.pickupLng}
                                        onChange={handleChange}
                                        required
                                        className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all text-sm"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Dotted separator */}
                        <div className="flex items-center gap-3">
                            <div className="flex-1 border-t border-slate-700 border-dashed"></div>
                            <div className="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center">
                                <svg className="w-4 h-4 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
                                </svg>
                            </div>
                            <div className="flex-1 border-t border-slate-700 border-dashed"></div>
                        </div>

                        {/* Dropoff */}
                        <div>
                            <h3 className="text-white font-semibold mb-4 flex items-center gap-2">
                                <span className="w-6 h-6 rounded-full bg-red-500 flex items-center justify-center text-white text-xs font-bold">B</span>
                                Dropoff Location
                            </h3>
                            <div className="space-y-3">
                                <input
                                    id="dropoffAddress"
                                    name="dropoffAddress"
                                    type="text"
                                    placeholder="Dropoff address"
                                    value={formData.dropoffAddress}
                                    onChange={handleChange}
                                    required
                                    className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                />
                                <div className="grid grid-cols-2 gap-3">
                                    <input
                                        id="dropoffLat"
                                        name="dropoffLat"
                                        type="number"
                                        step="any"
                                        placeholder="Latitude"
                                        value={formData.dropoffLat}
                                        onChange={handleChange}
                                        required
                                        className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all text-sm"
                                    />
                                    <input
                                        id="dropoffLng"
                                        name="dropoffLng"
                                        type="number"
                                        step="any"
                                        placeholder="Longitude"
                                        value={formData.dropoffLng}
                                        onChange={handleChange}
                                        required
                                        className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all text-sm"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Ride Preferences */}
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label htmlFor="vehicleType" className="block text-slate-300 text-sm font-medium mb-2">Vehicle Type</label>
                                <select
                                    id="vehicleType"
                                    name="vehicleType"
                                    value={formData.vehicleType}
                                    onChange={handleChange}
                                    className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                >
                                    {VEHICLE_TYPES.map(type => (
                                        <option key={type} value={type}>{type}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label htmlFor="offeredPrice" className="block text-slate-300 text-sm font-medium mb-2">
                                    Your Offer Price <span className="text-slate-500">(optional)</span>
                                </label>
                                <div className="relative">
                                    <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 font-medium">PKR</span>
                                    <input
                                        id="offeredPrice"
                                        name="offeredPrice"
                                        type="number"
                                        step="any"
                                        min="0"
                                        placeholder="0"
                                        value={formData.offeredPrice}
                                        onChange={handleChange}
                                        className="w-full bg-slate-700/50 border border-slate-600 rounded-xl pl-14 pr-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                    />
                                </div>
                            </div>
                        </div>

                        {/* Estimates */}
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label htmlFor="estimatedDistanceKm" className="block text-slate-300 text-sm font-medium mb-2">Distance (km)</label>
                                <input
                                    id="estimatedDistanceKm"
                                    name="estimatedDistanceKm"
                                    type="number"
                                    step="any"
                                    min="0"
                                    placeholder="e.g. 5.2"
                                    value={formData.estimatedDistanceKm}
                                    onChange={handleChange}
                                    className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                />
                            </div>
                            <div>
                                <label htmlFor="estimatedDurationMin" className="block text-slate-300 text-sm font-medium mb-2">Est. Duration (min)</label>
                                <input
                                    id="estimatedDurationMin"
                                    name="estimatedDurationMin"
                                    type="number"
                                    min="0"
                                    placeholder="e.g. 15"
                                    value={formData.estimatedDurationMin}
                                    onChange={handleChange}
                                    className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all"
                                />
                            </div>
                        </div>

                        {/* Notes */}
                        <div>
                            <label htmlFor="notes" className="block text-slate-300 text-sm font-medium mb-2">
                                Notes <span className="text-slate-500">(optional)</span>
                            </label>
                            <textarea
                                id="notes"
                                name="notes"
                                rows={3}
                                placeholder="Any special instructions for the driver..."
                                value={formData.notes}
                                onChange={handleChange}
                                className="w-full bg-slate-700/50 border border-slate-600 rounded-xl px-4 py-3 text-white placeholder-slate-400 focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-all resize-none"
                            />
                        </div>

                        {/* Submit */}
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold py-4 rounded-xl transition-all duration-200 shadow-lg shadow-amber-500/25 hover:shadow-amber-500/40 text-lg"
                        >
                            {loading ? (
                                <span className="flex items-center justify-center gap-3">
                                    <svg className="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                                    </svg>
                                    Posting your ride...
                                </span>
                            ) : (
                                <span className="flex items-center justify-center gap-2">
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                                    </svg>
                                    Request Ride & Get Bids
                                </span>
                            )}
                        </button>
                    </form>
                </div>

                {/* Quick tip */}
                <div className="mt-6 flex items-start gap-3 bg-blue-500/10 border border-blue-500/20 rounded-xl p-4">
                    <svg className="w-5 h-5 text-blue-400 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <p className="text-blue-300 text-sm">
                        <strong>How it works:</strong> After posting, nearby drivers will place bids. You can compare bids and choose the best offer — just like InDrive!
                    </p>
                </div>
            </div>
        </div>
    );
}
