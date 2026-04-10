import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { rideService } from '../services/rideService';

const ACTIVE_STATUSES  = ['SEARCHING', 'BIDDING', 'ACCEPTED', 'DRIVER_ARRIVING', 'IN_PROGRESS'];
const PAST_STATUSES    = ['COMPLETED', 'CANCELLED'];

const STATUS_META = {
    SEARCHING:      { label: 'Searching',      badge: 'bg-blue-500/20 text-blue-400 border-blue-500/30',   dot: 'bg-blue-400 animate-pulse' },
    BIDDING:        { label: 'Bids Received',  badge: 'bg-amber-500/20 text-amber-400 border-amber-500/30', dot: 'bg-amber-400 animate-pulse' },
    ACCEPTED:       { label: 'Driver Accepted',badge: 'bg-green-500/20 text-green-400 border-green-500/30', dot: 'bg-green-400 animate-pulse' },
    DRIVER_ARRIVING:{ label: 'Driver Arriving',badge: 'bg-cyan-500/20 text-cyan-400 border-cyan-500/30',    dot: 'bg-cyan-400 animate-pulse' },
    IN_PROGRESS:    { label: 'In Progress',    badge: 'bg-violet-500/20 text-violet-400 border-violet-500/30', dot: 'bg-violet-400 animate-pulse' },
    COMPLETED:      { label: 'Completed',      badge: 'bg-slate-600/40 text-slate-300 border-slate-600/40',  dot: 'bg-slate-400' },
    CANCELLED:      { label: 'Cancelled',      badge: 'bg-red-500/20 text-red-400 border-red-500/30',        dot: 'bg-red-400' },
};

export default function MyRidesPage() {
    const navigate   = useNavigate();
    const [rides,       setRides]      = useState([]);
    const [loading,     setLoading]    = useState(true);
    const [tab,         setTab]        = useState('active');  // 'active' | 'history'
    const [page,        setPage]       = useState(0);
    const [totalPages,  setTotalPages] = useState(0);

    useEffect(() => {
        fetchRides();
        // Auto-refresh active tab every 10 s so status changes appear
        const interval = tab === 'active' ? setInterval(fetchRides, 10000) : null;
        return () => { if (interval) clearInterval(interval); };
    }, [page, tab]);

    const fetchRides = async () => {
        setLoading(true);
        try {
            const res = await rideService.getMyRidesAsPassenger(page, 20);
            setRides(res.data.content || []);
            setTotalPages(res.data.totalPages || 0);
        } catch (err) {
            console.error('Error fetching rides:', err);
        } finally {
            setLoading(false);
        }
    };

    // split for tab view
    const activeRides  = rides.filter(r => ACTIVE_STATUSES.includes(r.status));
    const pastRides    = rides.filter(r => PAST_STATUSES.includes(r.status));
    const displayRides = tab === 'active' ? activeRides : pastRides;

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 py-6 px-4">
            <div className="max-w-2xl mx-auto">

                {/* Header */}
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h1 className="text-2xl font-bold text-white">My Rides</h1>
                        <p className="text-slate-400 text-sm mt-0.5">Active and past ride requests</p>
                    </div>
                    <Link
                        to="/rides/request"
                        className="flex items-center gap-2 bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white font-bold px-4 py-2.5 rounded-xl transition-all shadow-lg shadow-amber-500/20 text-sm"
                    >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                        </svg>
                        New Ride
                    </Link>
                </div>

                {/* Tabs */}
                <div className="flex gap-1 bg-slate-800/60 border border-slate-700/50 rounded-2xl p-1 mb-6">
                    {[
                        { key: 'active',  label: 'Active Rides', count: activeRides.length  },
                        { key: 'history', label: 'History',       count: pastRides.length    },
                    ].map(t => (
                        <button
                            key={t.key}
                            onClick={() => { setTab(t.key); setPage(0); }}
                            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                                tab === t.key
                                    ? 'bg-slate-700 text-white shadow'
                                    : 'text-slate-400 hover:text-white'
                            }`}
                        >
                            {t.label}
                            {t.count > 0 && (
                                <span className={`text-xs rounded-full px-2 py-0.5 font-bold ${
                                    tab === t.key
                                        ? t.key === 'active' ? 'bg-amber-500 text-white' : 'bg-slate-600 text-slate-200'
                                        : 'bg-slate-700 text-slate-400'
                                }`}>
                                    {t.count}
                                </span>
                            )}
                        </button>
                    ))}
                </div>

                {/* Content */}
                {loading ? (
                    <div className="flex justify-center py-16">
                        <div className="w-10 h-10 border-4 border-amber-500 border-t-transparent rounded-full animate-spin" />
                    </div>
                ) : displayRides.length === 0 ? (
                    <EmptyState tab={tab} navigate={navigate} />
                ) : (
                    <div className="space-y-3">
                        {displayRides.map(ride => (
                            <RideCard key={ride.id} ride={ride} navigate={navigate} />
                        ))}

                        {/* Pagination (history tab) */}
                        {tab === 'history' && totalPages > 1 && (
                            <div className="flex items-center justify-center gap-3 pt-4">
                                <button
                                    onClick={() => setPage(p => Math.max(0, p - 1))}
                                    disabled={page === 0}
                                    className="px-4 py-2 bg-slate-700 disabled:opacity-40 text-white rounded-lg hover:bg-slate-600 transition-colors text-sm"
                                >
                                    ← Prev
                                </button>
                                <span className="text-slate-400 text-sm">Page {page + 1} of {totalPages}</span>
                                <button
                                    onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                    disabled={page >= totalPages - 1}
                                    className="px-4 py-2 bg-slate-700 disabled:opacity-40 text-white rounded-lg hover:bg-slate-600 transition-colors text-sm"
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

// ─── Sub-components ───────────────────────────────────────────────────────────

function RideCard({ ride, navigate }) {
    const meta     = STATUS_META[ride.status] || STATUS_META.SEARCHING;
    const isActive = ACTIVE_STATUSES.includes(ride.status);
    const showLive = ['ACCEPTED', 'DRIVER_ARRIVING', 'IN_PROGRESS'].includes(ride.status);

    return (
        <div
            onClick={() => navigate(`/rides/${ride.id}`)}
            className={`bg-slate-800/60 border rounded-2xl p-5 cursor-pointer transition-all group ${
                isActive
                    ? 'border-amber-500/20 hover:border-amber-500/50 hover:bg-slate-800'
                    : 'border-slate-700/50 hover:border-slate-600'
            }`}
        >
            <div className="flex items-start gap-4">
                {/* Route column */}
                <div className="flex flex-col items-center mt-1.5 flex-shrink-0">
                    <div className="w-2.5 h-2.5 rounded-full bg-green-400"></div>
                    <div className="w-0.5 h-7 bg-slate-600 my-0.5"></div>
                    <div className="w-2.5 h-2.5 rounded-sm bg-red-400"></div>
                </div>

                {/* Text */}
                <div className="flex-1 min-w-0">
                    <p className="text-white font-semibold text-sm truncate">{ride.pickupAddress}</p>
                    <p className="text-slate-400 text-sm truncate mt-0.5">{ride.dropoffAddress}</p>

                    {/* Meta row */}
                    <div className="flex items-center gap-2 mt-2 flex-wrap">
                        {/* Status dot + badge */}
                        <span className={`inline-flex items-center gap-1.5 text-xs font-semibold px-2.5 py-0.5 rounded-full border ${meta.badge}`}>
                            <span className={`w-1.5 h-1.5 rounded-full ${meta.dot}`}></span>
                            {meta.label}
                        </span>

                        {ride.bidCount > 0 && (
                            <span className="text-xs text-amber-400 bg-amber-500/10 border border-amber-500/20 rounded-full px-2 py-0.5">
                                {ride.bidCount} bid{ride.bidCount !== 1 ? 's' : ''}
                            </span>
                        )}

                        <span className="text-xs text-slate-500">{new Date(ride.createdAt).toLocaleDateString()}</span>
                        <span className="text-xs text-slate-600">•</span>
                        <span className="text-xs text-slate-500">{ride.vehicleType}</span>
                    </div>
                </div>

                {/* Right side */}
                <div className="flex flex-col items-end gap-2 flex-shrink-0">
                    <p className="text-white font-bold">
                        {ride.finalPrice
                            ? `PKR ${ride.finalPrice}`
                            : ride.offeredPrice
                                ? `PKR ${ride.offeredPrice}`
                                : <span className="text-slate-400 font-normal text-sm">Open bid</span>}
                    </p>
                    <svg className="w-4 h-4 text-slate-600 group-hover:text-amber-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                </div>
            </div>

            {/* Live Track CTA for active rides */}
            {showLive && (
                <div className="mt-4 pt-3 border-t border-slate-700/50">
                    <button
                        onClick={e => { e.stopPropagation(); navigate(`/rides/${ride.id}/live`); }}
                        className="w-full flex items-center justify-center gap-2 bg-green-500/10 border border-green-500/30 hover:bg-green-500/20 text-green-400 font-semibold text-sm py-2 rounded-xl transition-all"
                    >
                        <span className="w-2 h-2 rounded-full bg-green-400 animate-pulse"></span>
                        Live Track Driver
                    </button>
                </div>
            )}

            {/* Accept Bid CTA */}
            {ride.status === 'BIDDING' && ride.bidCount > 0 && (
                <div className="mt-4 pt-3 border-t border-slate-700/50">
                    <button
                        onClick={e => { e.stopPropagation(); navigate(`/rides/${ride.id}`); }}
                        className="w-full flex items-center justify-center gap-2 bg-amber-500/10 border border-amber-500/30 hover:bg-amber-500/20 text-amber-400 font-semibold text-sm py-2 rounded-xl transition-all"
                    >
                        👀 View {ride.bidCount} Bid{ride.bidCount !== 1 ? 's' : ''} — Choose a Driver
                    </button>
                </div>
            )}
        </div>
    );
}

function EmptyState({ tab, navigate }) {
    if (tab === 'active') {
        return (
            <div className="text-center py-16">
                <div className="w-20 h-20 bg-amber-500/10 border border-amber-500/20 rounded-full flex items-center justify-center mx-auto mb-5">
                    <svg className="w-9 h-9 text-amber-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                    </svg>
                </div>
                <h3 className="text-white text-xl font-bold mb-2">No active rides</h3>
                <p className="text-slate-400 text-sm mb-6">Book a ride and nearby drivers will bid for you</p>
                <button
                    onClick={() => navigate('/rides/request')}
                    className="bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-white font-bold px-8 py-3 rounded-2xl transition-all shadow-lg shadow-amber-500/20"
                >
                    Book a Ride Now
                </button>
            </div>
        );
    }
    return (
        <div className="text-center py-16 text-slate-500">
            <p className="text-lg">No completed or cancelled rides yet.</p>
        </div>
    );
}
