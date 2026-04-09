import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';

const STATUS_CONFIG = {
    SEARCHING: { label: 'Searching for drivers...', color: 'text-blue-400', bg: 'bg-blue-500/10 border-blue-500/30', dot: 'bg-blue-400' },
    BIDDING: { label: 'Bids received!', color: 'text-amber-400', bg: 'bg-amber-500/10 border-amber-500/30', dot: 'bg-amber-400' },
    ACCEPTED: { label: 'Driver accepted', color: 'text-green-400', bg: 'bg-green-500/10 border-green-500/30', dot: 'bg-green-400' },
    DRIVER_ARRIVING: { label: 'Driver is on the way', color: 'text-cyan-400', bg: 'bg-cyan-500/10 border-cyan-500/30', dot: 'bg-cyan-400' },
    IN_PROGRESS: { label: 'Ride in progress', color: 'text-violet-400', bg: 'bg-violet-500/10 border-violet-500/30', dot: 'bg-violet-400' },
    COMPLETED: { label: 'Ride completed', color: 'text-green-400', bg: 'bg-green-500/10 border-green-500/30', dot: 'bg-green-400' },
    CANCELLED: { label: 'Ride cancelled', color: 'text-red-400', bg: 'bg-red-500/10 border-red-500/30', dot: 'bg-red-400' },
};

export default function RideDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [ride, setRide] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        fetchRide();
        // Auto-refresh every 5 seconds for live status
        const interval = setInterval(() => {
            if (ride && !['COMPLETED', 'CANCELLED'].includes(ride.status)) {
                fetchRide();
            }
        }, 5000);
        return () => clearInterval(interval);
    }, [id]);

    const fetchRide = async () => {
        try {
            const res = await rideService.getRideById(id);
            setRide(res.data);
        } catch (err) {
            setError('Failed to load ride details');
        } finally {
            setLoading(false);
        }
    };

    const handleAcceptBid = async (bidId) => {
        setActionLoading(true);
        try {
            const res = await rideService.acceptBid(id, bidId);
            setRide(res.data);
        } catch (err) {
            alert(err.response?.data?.statusMessage || 'Failed to accept bid');
        } finally {
            setActionLoading(false);
        }
    };

    const handleCancelRide = async () => {
        const reason = prompt('Reason for cancellation (optional):');
        setActionLoading(true);
        try {
            const res = await rideService.cancelRideByPassenger(id, reason);
            setRide(res.data);
        } catch (err) {
            alert(err.response?.data?.statusMessage || 'Failed to cancel ride');
        } finally {
            setActionLoading(false);
        }
    };

    if (loading) return (
        <div className="min-h-screen bg-slate-900 flex items-center justify-center">
            <div className="flex flex-col items-center gap-4">
                <div className="w-12 h-12 border-4 border-amber-500 border-t-transparent rounded-full animate-spin"></div>
                <p className="text-slate-400">Loading ride details...</p>
            </div>
        </div>
    );

    if (error || !ride) return (
        <div className="min-h-screen bg-slate-900 flex items-center justify-center">
            <div className="text-center">
                <p className="text-red-400 text-lg mb-4">{error || 'Ride not found'}</p>
                <button onClick={() => navigate('/rides/my')} className="text-amber-400 hover:text-amber-300">
                    ← Back to My Rides
                </button>
            </div>
        </div>
    );

    const statusCfg = STATUS_CONFIG[ride.status] || STATUS_CONFIG.SEARCHING;
    const isActive = !['COMPLETED', 'CANCELLED'].includes(ride.status);
    const canCancel = ['SEARCHING', 'BIDDING'].includes(ride.status);

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 py-8 px-4">
            <div className="max-w-3xl mx-auto">

                {/* Back link */}
                <button onClick={() => navigate('/rides/my')} className="flex items-center gap-2 text-slate-400 hover:text-white mb-6 transition-colors">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                    </svg>
                    Back to My Rides
                </button>

                {/* Status Banner */}
                <div className={`border rounded-2xl p-4 mb-6 flex items-center gap-3 ${statusCfg.bg}`}>
                    <span className={`w-3 h-3 rounded-full ${statusCfg.dot} ${isActive ? 'animate-pulse' : ''}`}></span>
                    <span className={`font-semibold ${statusCfg.color}`}>{statusCfg.label}</span>
                    {isActive && <span className="ml-auto text-slate-500 text-xs">Auto-refreshing...</span>}
                </div>

                {/* Ride Info Card */}
                <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6 mb-6">
                    <h1 className="text-2xl font-bold text-white mb-6">Ride #{ride.id}</h1>

                    <div className="space-y-4">
                        {/* Route */}
                        <div className="flex gap-4">
                            <div className="flex flex-col items-center pt-1">
                                <div className="w-3 h-3 rounded-full bg-green-500"></div>
                                <div className="w-0.5 h-12 bg-slate-600 my-1"></div>
                                <div className="w-3 h-3 rounded-full bg-red-500"></div>
                            </div>
                            <div className="flex-1 space-y-4">
                                <div>
                                    <p className="text-slate-400 text-xs mb-1">PICKUP</p>
                                    <p className="text-white font-medium">{ride.pickupAddress}</p>
                                    <p className="text-slate-500 text-xs">{ride.pickupLat}, {ride.pickupLng}</p>
                                </div>
                                <div>
                                    <p className="text-slate-400 text-xs mb-1">DROPOFF</p>
                                    <p className="text-white font-medium">{ride.dropoffAddress}</p>
                                    <p className="text-slate-500 text-xs">{ride.dropoffLat}, {ride.dropoffLng}</p>
                                </div>
                            </div>
                        </div>

                        {/* Metadata Grid */}
                        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-slate-700">
                            <div>
                                <p className="text-slate-400 text-xs">Vehicle</p>
                                <p className="text-white font-medium">{ride.vehicleType}</p>
                            </div>
                            <div>
                                <p className="text-slate-400 text-xs">Your Offer</p>
                                <p className="text-white font-medium">
                                    {ride.offeredPrice ? `PKR ${ride.offeredPrice}` : 'Open to bids'}
                                </p>
                            </div>
                            {ride.estimatedDistanceKm && (
                                <div>
                                    <p className="text-slate-400 text-xs">Distance</p>
                                    <p className="text-white font-medium">{ride.estimatedDistanceKm} km</p>
                                </div>
                            )}
                            {ride.estimatedDurationMin && (
                                <div>
                                    <p className="text-slate-400 text-xs">Est. Duration</p>
                                    <p className="text-white font-medium">{ride.estimatedDurationMin} min</p>
                                </div>
                            )}
                        </div>

                        {ride.notes && (
                            <div className="bg-slate-700/40 rounded-xl p-3 text-sm">
                                <p className="text-slate-400 text-xs mb-1">NOTES</p>
                                <p className="text-slate-300">{ride.notes}</p>
                            </div>
                        )}
                    </div>
                </div>

                {/* Assigned Driver Card (after acceptance) */}
                {ride.assignedDriverId && (
                    <div className="bg-gradient-to-r from-green-500/10 to-emerald-500/10 border border-green-500/30 rounded-2xl p-6 mb-6">
                        <h3 className="text-green-400 font-semibold mb-4 flex items-center gap-2">
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                            </svg>
                            Your Driver
                        </h3>
                        <div className="flex items-center gap-4">
                            <div className="w-14 h-14 rounded-full bg-slate-700 flex items-center justify-center text-2xl font-bold text-white">
                                {ride.assignedDriverName?.charAt(0)}
                            </div>
                            <div>
                                <p className="text-white font-semibold text-lg">{ride.assignedDriverName}</p>
                                <div className="flex items-center gap-2 mt-1">
                                    <span className="text-amber-400 text-sm">★ {ride.assignedDriverRating}</span>
                                    {ride.assignedDriverPhone && (
                                        <a href={`tel:${ride.assignedDriverPhone}`} className="text-blue-400 hover:text-blue-300 text-sm">
                                            📞 {ride.assignedDriverPhone}
                                        </a>
                                    )}
                                </div>
                            </div>
                            <div className="ml-auto text-right">
                                <p className="text-slate-400 text-xs">Final Price</p>
                                <p className="text-white text-2xl font-bold">PKR {ride.finalPrice}</p>
                            </div>
                        </div>
                    </div>
                )}

                {/* Completed Ride Summary */}
                {ride.status === 'COMPLETED' && (
                    <div className="bg-green-500/10 border border-green-500/30 rounded-2xl p-6 mb-6">
                        <h3 className="text-green-400 font-semibold mb-4">Ride Summary</h3>
                        <div className="grid grid-cols-3 gap-4">
                            <div className="text-center">
                                <p className="text-slate-400 text-xs">Total Paid</p>
                                <p className="text-white font-bold text-xl">PKR {ride.finalPrice}</p>
                            </div>
                            <div className="text-center">
                                <p className="text-slate-400 text-xs">Commission ({ride.commissionRate}%)</p>
                                <p className="text-orange-400 font-bold text-xl">PKR {ride.commissionAmount}</p>
                            </div>
                            <div className="text-center">
                                <p className="text-slate-400 text-xs">Driver Earned</p>
                                <p className="text-green-400 font-bold text-xl">PKR {ride.driverEarning}</p>
                            </div>
                        </div>
                    </div>
                )}

                {/* Bids Section */}
                {['SEARCHING', 'BIDDING'].includes(ride.status) && (
                    <div className="bg-slate-800/60 border border-slate-700/50 rounded-2xl p-6 mb-6">
                        <h2 className="text-white font-semibold text-lg mb-4">
                            Driver Bids
                            {ride.bids?.length > 0 && (
                                <span className="ml-2 bg-amber-500/20 text-amber-400 text-sm rounded-full px-2 py-0.5">
                                    {ride.bids.length}
                                </span>
                            )}
                        </h2>

                        {!ride.bids?.length ? (
                            <div className="text-center py-8">
                                <div className="w-16 h-16 bg-slate-700/50 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <svg className="w-8 h-8 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                                    </svg>
                                </div>
                                <p className="text-slate-400">Waiting for nearby drivers to bid...</p>
                                <p className="text-slate-500 text-sm mt-1">This usually takes 1-3 minutes</p>
                            </div>
                        ) : (
                            <div className="space-y-3">
                                {ride.bids.filter(b => b.status === 'PENDING').map(bid => (
                                    <div key={bid.id} className="bg-slate-700/50 border border-slate-600/50 rounded-xl p-4 flex items-center gap-4 hover:border-amber-500/30 transition-all">
                                        <div className="w-12 h-12 rounded-full bg-slate-600 flex items-center justify-center text-white font-bold text-lg flex-shrink-0">
                                            {bid.driverName?.charAt(0)}
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-white font-semibold">{bid.driverName}</p>
                                            <div className="flex items-center gap-3 mt-1">
                                                <span className="text-amber-400 text-sm">★ {bid.driverRating}</span>
                                                <span className="text-slate-400 text-sm">{bid.driverTotalTrips} trips</span>
                                                {bid.estimatedArrivalMin && (
                                                    <span className="text-slate-400 text-sm">🕐 {bid.estimatedArrivalMin} min away</span>
                                                )}
                                            </div>
                                            {bid.message && (
                                                <p className="text-slate-400 text-xs mt-1 truncate">"{bid.message}"</p>
                                            )}
                                        </div>
                                        <div className="text-right flex-shrink-0">
                                            <p className="text-white font-bold text-xl">PKR {bid.bidAmount}</p>
                                            <button
                                                onClick={() => handleAcceptBid(bid.id)}
                                                disabled={actionLoading}
                                                className="mt-2 bg-amber-500 hover:bg-amber-400 disabled:opacity-50 text-white font-semibold text-sm px-4 py-1.5 rounded-lg transition-colors"
                                            >
                                                Accept
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* Actions */}
                {canCancel && (
                    <button
                        onClick={handleCancelRide}
                        disabled={actionLoading}
                        className="w-full border border-red-500/40 text-red-400 hover:bg-red-500/10 font-semibold py-3 rounded-xl transition-all"
                    >
                        Cancel Ride
                    </button>
                )}
            </div>
        </div>
    );
}
