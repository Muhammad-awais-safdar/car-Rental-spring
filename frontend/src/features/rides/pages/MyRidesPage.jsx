import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';

const STATUS_BADGE = {
    SEARCHING: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
    BIDDING: 'bg-amber-500/20 text-amber-400 border-amber-500/30',
    ACCEPTED: 'bg-green-500/20 text-green-400 border-green-500/30',
    DRIVER_ARRIVING: 'bg-cyan-500/20 text-cyan-400 border-cyan-500/30',
    IN_PROGRESS: 'bg-violet-500/20 text-violet-400 border-violet-500/30',
    COMPLETED: 'bg-slate-500/20 text-slate-300 border-slate-500/30',
    CANCELLED: 'bg-red-500/20 text-red-400 border-red-500/30',
};

export default function MyRidesPage() {
    const navigate = useNavigate();
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        fetchMyRides();
    }, [page]);

    const fetchMyRides = async () => {
        setLoading(true);
        try {
            const res = await rideService.getMyRidesAsPassenger(page, 10);
            setRides(res.data.content || []);
            setTotalPages(res.data.totalPages || 0);
        } catch (err) {
            console.error('Error fetching rides:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 py-8 px-4">
            <div className="max-w-4xl mx-auto">

                {/* Header */}
                <div className="flex items-center justify-between mb-8">
                    <div>
                        <h1 className="text-3xl font-bold text-white mb-1">My Rides</h1>
                        <p className="text-slate-400">Track your ride history and active rides</p>
                    </div>
                    <button
                        onClick={() => navigate('/rides/request')}
                        className="bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white font-bold px-5 py-2.5 rounded-xl flex items-center gap-2 transition-all shadow-lg shadow-amber-500/20"
                    >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                        </svg>
                        New Ride
                    </button>
                </div>

                {loading ? (
                    <div className="flex items-center justify-center py-20">
                        <div className="w-10 h-10 border-4 border-amber-500 border-t-transparent rounded-full animate-spin"></div>
                    </div>
                ) : rides.length === 0 ? (
                    <div className="text-center py-20">
                        <div className="w-20 h-20 bg-slate-700/50 rounded-full flex items-center justify-center mx-auto mb-6">
                            <svg className="w-10 h-10 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                            </svg>
                        </div>
                        <h3 className="text-white text-xl font-semibold mb-2">No rides yet</h3>
                        <p className="text-slate-400 mb-6">Request your first ride and get competitive bids from drivers</p>
                        <button
                            onClick={() => navigate('/rides/request')}
                            className="bg-amber-500 hover:bg-amber-400 text-white font-bold px-6 py-3 rounded-xl transition-colors"
                        >
                            Request a Ride
                        </button>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {rides.map(ride => (
                            <div
                                key={ride.id}
                                onClick={() => navigate(`/rides/${ride.id}`)}
                                className="bg-slate-800/60 border border-slate-700/50 hover:border-amber-500/30 rounded-2xl p-5 cursor-pointer transition-all hover:bg-slate-800/80 group"
                            >
                                <div className="flex items-start justify-between gap-4">
                                    <div className="flex-1 min-w-0">
                                        {/* Route */}
                                        <div className="flex items-start gap-3 mb-3">
                                            <div className="flex flex-col items-center mt-1 flex-shrink-0">
                                                <div className="w-2.5 h-2.5 rounded-full bg-green-500"></div>
                                                <div className="w-0.5 h-6 bg-slate-600 my-0.5"></div>
                                                <div className="w-2.5 h-2.5 rounded-full bg-red-500"></div>
                                            </div>
                                            <div className="space-y-2 flex-1 min-w-0">
                                                <p className="text-white font-medium truncate">{ride.pickupAddress}</p>
                                                <p className="text-slate-400 truncate">{ride.dropoffAddress}</p>
                                            </div>
                                        </div>

                                        {/* Meta */}
                                        <div className="flex items-center gap-4 text-sm">
                                            <span className="text-slate-400">{new Date(ride.createdAt).toLocaleDateString()}</span>
                                            <span className="text-slate-500">•</span>
                                            <span className="text-slate-400">{ride.vehicleType}</span>
                                            {ride.bidCount > 0 && (
                                                <>
                                                    <span className="text-slate-500">•</span>
                                                    <span className="text-amber-400">{ride.bidCount} bid{ride.bidCount !== 1 ? 's' : ''}</span>
                                                </>
                                            )}
                                        </div>
                                    </div>

                                    <div className="flex flex-col items-end gap-2 flex-shrink-0">
                                        {/* Status badge */}
                                        <span className={`text-xs font-semibold px-2.5 py-1 rounded-full border ${STATUS_BADGE[ride.status] || STATUS_BADGE.SEARCHING}`}>
                                            {ride.status.replace(/_/g, ' ')}
                                        </span>

                                        {/* Price */}
                                        <p className="text-white font-bold text-lg">
                                            {ride.finalPrice
                                                ? `PKR ${ride.finalPrice}`
                                                : ride.offeredPrice
                                                    ? `PKR ${ride.offeredPrice}`
                                                    : 'Open bid'}
                                        </p>

                                        <svg className="w-4 h-4 text-slate-500 group-hover:text-amber-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        ))}

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="flex items-center justify-center gap-3 pt-4">
                                <button
                                    onClick={() => setPage(p => Math.max(0, p - 1))}
                                    disabled={page === 0}
                                    className="px-4 py-2 bg-slate-700 disabled:opacity-40 text-white rounded-lg hover:bg-slate-600 transition-colors"
                                >
                                    ← Prev
                                </button>
                                <span className="text-slate-400 text-sm">Page {page + 1} of {totalPages}</span>
                                <button
                                    onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                    disabled={page >= totalPages - 1}
                                    className="px-4 py-2 bg-slate-700 disabled:opacity-40 text-white rounded-lg hover:bg-slate-600 transition-colors"
                                >
                                    Next →
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
