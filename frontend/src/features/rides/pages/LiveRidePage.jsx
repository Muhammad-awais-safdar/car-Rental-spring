import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';
import { useAuth } from '../../auth/context/AuthContext';
import { useWebSocket } from '../../../core/hooks/useWebSocket';
import LiveTrackingMap from '../../tracking/components/LiveTrackingMap';

/**
 * LiveRidePage
 * Full-screen immersive tracking page for active rides.
 */
export default function LiveRidePage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { token } = useAuth();
    
    const [ride, setRide] = useState(null);
    const [loading, setLoading] = useState(true);
    const [driverLoc, setDriverLoc] = useState(null);

    const { subscribe } = useWebSocket(token);

    useEffect(() => {
        fetchRide();
    }, [id]);

    useEffect(() => {
        if (!id) return;

        // Sub to status changes
        const unsubStatus = subscribe(`/topic/ride/${id}/status`, (updated) => {
            setRide(updated);
            if (updated.status === 'COMPLETED') {
                alert('Ride completed! Hope you enjoyed the trip.');
                navigate('/rides/my');
            }
        });

        // Sub to location changes
        const unsubLoc = subscribe(`/topic/ride/${id}/location`, (loc) => {
            setDriverLoc([loc.latitude, loc.longitude]);
        });

        return () => {
            unsubStatus();
            unsubLoc();
        };
    }, [id, subscribe, navigate]);

    const fetchRide = async () => {
        try {
            const res = await rideService.getRideById(id);
            setRide(res.data);
            if (res.data.driverLat) setDriverLoc([res.data.driverLat, res.data.driverLng]);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="h-screen flex items-center justify-center bg-slate-900 text-white">Connecting...</div>;
    if (!ride) return <div className="p-10 text-center">Ride not found</div>;

    return (
        <div className="relative h-screen w-full bg-slate-900 overflow-hidden">
            {/* Full Screen Map */}
            <div className="absolute inset-0 z-0">
                <LiveTrackingMap 
                    token={token}
                    driverId={ride.driverId}
                    pickupCoords={[ride.pickupLat, ride.pickupLng]}
                    dropoffCoords={[ride.dropoffLat, ride.dropoffLng]}
                    initialDriverLocation={driverLoc}
                />
            </div>

            {/* Top Navigation Overlay */}
            <div className="absolute top-6 left-6 right-6 z-10 flex justify-between items-start pointer-events-none">
                <button 
                    onClick={() => navigate(-1)}
                    className="p-3 bg-white/90 backdrop-blur rounded-full shadow-lg pointer-events-auto hover:bg-white transition-all transition-transform active:scale-90"
                >
                    <svg className="w-6 h-6 text-slate-900" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                </button>

                <div className="bg-white/90 backdrop-blur px-6 py-3 rounded-2xl shadow-lg border border-white/20 pointer-events-auto flex items-center gap-4">
                    <div className="h-2 w-2 rounded-full bg-green-500 animate-pulse" />
                    <div>
                        <div className="text-[10px] font-bold text-slate-400 uppercase tracking-widest leading-none">Status</div>
                        <div className="text-sm font-black text-slate-900 uppercase">{ride.status}</div>
                    </div>
                </div>
            </div>

            {/* Bottom Trip Info Card */}
            <div className="absolute bottom-8 left-1/2 -translate-x-1/2 w-[calc(100%-48px)] max-w-lg z-10">
                <div className="bg-white rounded-[32px] p-6 shadow-2xl border border-slate-100">
                    {/* Progress Bar (simplified) */}
                    <div className="w-full h-1 bg-slate-100 rounded-full mb-6 relative overflow-hidden">
                        <div className="absolute left-0 top-0 h-full bg-red-600 transition-all duration-1000" style={{ width: '45%' }} />
                    </div>

                    <div className="flex items-center justify-between mb-8">
                        <div className="flex items-center gap-4">
                            <div className="w-14 h-14 bg-slate-100 rounded-2xl overflow-hidden border border-slate-100 shadow-inner">
                                <img src={`https://ui-avatars.com/api/?name=${ride.driverName || 'Driver'}&background=random`} alt="driver" />
                            </div>
                            <div>
                                <h3 className="text-lg font-black text-slate-900 leading-tight">{ride.driverName || 'Finding Driver...'}</h3>
                                <div className="flex items-center gap-1 text-amber-500">
                                    <span className="text-xs font-bold">⭐ 4.9</span>
                                    <span className="text-slate-300 mx-1">•</span>
                                    <span className="text-[10px] font-bold text-slate-400 uppercase tracking-tight">{ride.vehicleInfo || 'White sedan'}</span>
                                </div>
                            </div>
                        </div>
                        <div className="text-right">
                            <div className="text-2xl font-black text-slate-900 leading-none mb-1">$ {ride.offeredPrice}</div>
                            <div className="text-[10px] font-bold text-slate-400 uppercase">Fixed Price</div>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="bg-slate-50 p-4 rounded-2xl flex items-center gap-3">
                            <div className="text-xl">⏱️</div>
                            <div>
                                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-tight">Arrival</div>
                                <div className="text-sm font-bold text-slate-900">12 Mins</div>
                            </div>
                        </div>
                        <button className="bg-slate-900 text-white p-4 rounded-2xl flex items-center justify-center gap-2 hover:bg-black transition-all group">
                            <span className="text-sm font-bold">Contact</span>
                            <span className="group-hover:translate-x-1 transition-transform">💬</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
