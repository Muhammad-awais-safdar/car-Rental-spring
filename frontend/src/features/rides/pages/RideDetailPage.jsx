import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';
import { useAuth } from '../../auth/context/AuthContext';
import { useWebSocket } from '../../../core/hooks/useWebSocket';
import LiveTrackingMap from '../../tracking/components/LiveTrackingMap';

export default function RideDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { token, user } = useAuth();
    
    const [ride, setRide] = useState(null);
    const [bids, setBids] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const { subscribe } = useWebSocket(token);

    useEffect(() => {
        fetchRideDetails();
    }, [id]);

    useEffect(() => {
        if (!id) return;

        // Subscribe to incoming bids for this ride
        const unsubBids = subscribe(`/topic/ride/${id}/bids`, (newBid) => {
            setBids(prev => {
                // Avoid duplicates
                if (prev.find(b => b.id === newBid.id)) return prev;
                return [newBid, ...prev];
            });
        });

        // Subscribe to ride status updates (e.g. if driver starts it or it's cancelled)
        const unsubStatus = subscribe(`/topic/ride/${id}/status`, (updatedRide) => {
            setRide(updatedRide);
            if (updatedRide.status === 'ACCEPTED' || updatedRide.status === 'IN_PROGRESS') {
                navigate(`/rides/${id}/live`);
            }
        });

        return () => {
            unsubBids();
            unsubStatus();
        };
    }, [id, subscribe, navigate]);

    const fetchRideDetails = async () => {
        try {
            const response = await rideService.getRideById(id);
            setRide(response.data);
            setBids(response.data.bids || []);
        } catch (err) {
            setError('Failed to load ride details');
        } finally {
            setLoading(false);
        }
    };

    const handleAcceptBid = async (bidId) => {
        try {
            await rideService.acceptBid(id, bidId);
            // Redirection will be handled by the status WebSocket subscription
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to accept bid');
        }
    };

    if (loading) return <div className="flex items-center justify-center h-96"><div className="animate-spin text-3xl">🚗</div></div>;
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>;
    if (!ride) return <div className="p-8 text-center">Ride not found</div>;

    const isPending = ride.status === 'PENDING' || ride.status === 'REQUESTED';

    return (
        <div className="max-w-6xl mx-auto px-4 py-8">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                
                {/* Status & Details */}
                <div className="lg:col-span-1 space-y-6">
                    <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
                        <div className="flex items-center justify-between mb-4">
                            <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${
                                ride.status === 'PENDING' ? 'bg-amber-100 text-amber-700' : 'bg-green-100 text-green-700'
                            }`}>
                                {ride.status}
                            </span>
                            <span className="text-slate-400 text-xs">ID: #{id}</span>
                        </div>
                        
                        <h1 className="text-xl font-bold text-slate-900 mb-6">Ride Request</h1>

                        <div className="space-y-4">
                            <div className="flex gap-4">
                                <div className="flex flex-col items-center pt-1">
                                    <div className="w-3 h-3 rounded-full bg-green-500" />
                                    <div className="w-0.5 h-12 bg-slate-100 my-1" />
                                    <div className="w-3 h-3 rounded-full bg-red-500" />
                                </div>
                                <div className="flex-1 space-y-4">
                                    <div>
                                        <div className="text-xs text-slate-400 font-bold uppercase">Pickup</div>
                                        <div className="text-sm font-medium">{ride.pickupAddress}</div>
                                    </div>
                                    <div>
                                        <div className="text-xs text-slate-400 font-bold uppercase">Dropoff</div>
                                        <div className="text-sm font-medium">{ride.dropoffAddress}</div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="mt-8 pt-6 border-t border-slate-50 flex justify-between items-center">
                            <div>
                                <div className="text-xs text-slate-400 font-bold uppercase">Estimated Fare</div>
                                <div className="text-lg font-bold text-slate-900">${ride.offeredPrice || 'Negotiable'}</div>
                            </div>
                            <div className="text-right">
                                <div className="text-xs text-slate-400 font-bold uppercase">Vehicle Type</div>
                                <div className="text-sm font-medium">{ride.vehicleType}</div>
                            </div>
                        </div>
                    </div>

                    {/* Passenger Info */}
                    <div className="bg-slate-50 rounded-2xl p-6 border border-slate-100">
                        <h3 className="text-sm font-bold text-slate-900 mb-4 uppercase tracking-wider">Your Request</h3>
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center text-red-600 font-bold">
                                {user.firstName?.charAt(0)}
                            </div>
                            <div>
                                <div className="text-sm font-bold">{user.firstName} {user.lastName}</div>
                                <div className="text-xs text-slate-500">Waiting for driver bids...</div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Bids List or Active Map */}
                <div className="lg:col-span-2 space-y-6">
                    {isPending ? (
                        <div className="space-y-4">
                            <div className="flex items-center justify-between px-2">
                                <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                                    Driver Bids
                                    <span className="bg-red-500 text-white text-[10px] px-1.5 py-0.5 rounded-full animate-pulse">
                                        {bids.length}
                                    </span>
                                </h2>
                                <span className="text-xs text-slate-400">Updates in real-time</span>
                            </div>

                            {bids.length === 0 ? (
                                <div className="bg-white rounded-2xl p-12 text-center border border-dashed border-slate-200">
                                    <div className="text-4xl mb-4 animate-bounce">📡</div>
                                    <h3 className="font-bold text-slate-900 mb-1">Searching for nearby drivers</h3>
                                    <p className="text-slate-500 text-sm">We've notified 8 available drivers in your area. Hang tight!</p>
                                </div>
                            ) : (
                                <div className="grid gap-4">
                                    {bids.map(bid => (
                                        <div key={bid.id} className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 flex items-center justify-between hover:shadow-md transition-shadow">
                                            <div className="flex items-center gap-4">
                                                <div className="relative">
                                                    <div className="w-12 h-12 bg-slate-100 rounded-full overflow-hidden">
                                                        {/* Placeholder avatar */}
                                                        <img src={`https://ui-avatars.com/api/?name=${bid.driverName}&background=random`} alt="driver" />
                                                    </div>
                                                    <div className="absolute -bottom-1 -right-1 bg-white rounded-full p-1 shadow-sm">
                                                        <span className="text-[10px]">⭐ {bid.driverRating || '4.8'}</span>
                                                    </div>
                                                </div>
                                                <div>
                                                    <div className="text-sm font-bold text-slate-900">{bid.driverName}</div>
                                                    <div className="text-xs text-slate-500">{bid.vehicleInfo || 'Toyota Corolla • White'}</div>
                                                    <div className="text-[10px] text-green-600 font-bold mt-1">2 mins away</div>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-6">
                                                <div className="text-right">
                                                    <div className="text-xs text-slate-400 uppercase font-bold tracking-tight">Offer Price</div>
                                                    <div className="text-xl font-black text-red-600">${bid.offeredPrice}</div>
                                                </div>
                                                <button 
                                                    onClick={() => handleAcceptBid(bid.id)}
                                                    className="px-6 py-2.5 bg-slate-900 text-white text-sm font-bold rounded-xl hover:bg-black transition-all active:scale-95"
                                                >
                                                    Accept
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    ) : (
                        <div className="bg-white rounded-2xl p-2 shadow-sm border border-slate-100 h-[500px] overflow-hidden">
                            <LiveTrackingMap 
                                token={token}
                                driverId={ride.driverId}
                                pickupCoords={[ride.pickupLat, ride.pickupLng]}
                                dropoffCoords={[ride.dropoffLat, ride.dropoffLng]}
                            />
                        </div>
                    )}

                    {/* Nearby Drivers Map (Small) */}
                    {isPending && (
                        <div className="bg-white rounded-2xl p-1 shadow-sm border border-slate-100 h-64 relative overflow-hidden">
                            <LiveTrackingMap 
                                token={token}
                                pickupCoords={[ride.pickupLat, ride.pickupLng]}
                            />
                            <div className="absolute bottom-4 left-4 z-[1000] bg-white/90 backdrop-blur px-3 py-1.5 rounded-lg text-[10px] font-bold shadow-soft">
                                📡 SYSTEM: Notifying nearby drivers
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
