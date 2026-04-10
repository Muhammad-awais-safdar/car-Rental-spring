import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { rideService } from '../services/rideService';
import { useAuth } from '../../auth/context/AuthContext';
import LiveTrackingMap from '../../tracking/components/LiveTrackingMap';

const VEHICLE_TYPES = [
    { value: 'ANY',  label: 'Any Vehicle',  icon: '🚗', desc: 'All available' },
    { value: 'CAR',  label: 'Car',           icon: '🚙', desc: 'Sedan / Hatchback' },
    { value: 'BIKE', label: 'Bike',          icon: '🏍️', desc: 'Motorbike / Rickshaw' },
    { value: 'VAN',  label: 'Van / SUV',     icon: '🚐', desc: 'For groups' },
];

async function geocode(address) {
    if (!address) return null;
    try {
        const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(address)}&format=json&limit=1`;
        const res = await fetch(url, { headers: { 'Accept-Language': 'en' } });
        const data = await res.json();
        if (!data.length) return null;
        return { lat: parseFloat(data[0].lat), lng: parseFloat(data[0].lon) };
    } catch {
        return null;
    }
}

export default function RequestRidePage() {
    const navigate = useNavigate();
    const { token } = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [geoLoading, setGeoLoading] = useState(false);

    const [pickup,  setPickup]  = useState('');
    const [dropoff, setDropoff] = useState('');
    const [fare,    setFare]    = useState('');
    const [vehicle, setVehicle] = useState('ANY');
    const [notes,   setNotes]   = useState('');

    const [coords, setCoords] = useState({ pickup: null, dropoff: null });
    const [nearbyDrivers, setNearbyDrivers] = useState([]);

    // Get current location on mount
    useEffect(() => {
        if (!navigator.geolocation) return;
        setGeoLoading(true);
        navigator.geolocation.getCurrentPosition(
            async (pos) => {
                const { latitude, longitude } = pos.coords;
                setCoords(prev => ({ ...prev, pickup: [latitude, longitude] }));
                try {
                    const res = await fetch(
                        `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json`,
                        { headers: { 'Accept-Language': 'en' } }
                    );
                    const d = await res.json();
                    if (d.display_name) setPickup(d.display_name.split(',').slice(0, 3).join(',').trim());
                } catch {
                    setPickup(`${latitude.toFixed(5)}, ${longitude.toFixed(5)}`);
                }
                setGeoLoading(false);
                fetchNearbyDrivers(latitude, longitude);
            },
            () => setGeoLoading(false),
            { enableHighAccuracy: true, timeout: 6000 }
        );
    }, []);

    const fetchNearbyDrivers = async (lat, lng) => {
        try {
            // This endpoint should be in tracking service/controller
            const res = await fetch(`/api/tracking/drivers/online?lat=${lat}&lng=${lng}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const data = await res.json();
            if (data.data) setNearbyDrivers(data.data);
        } catch (err) {
            console.error('Failed to fetch nearby drivers:', err);
        }
    };

    const handlePreviewRoute = async () => {
        if (!pickup || !dropoff) return;
        setLoading(true);
        const [p, d] = await Promise.all([geocode(pickup), geocode(dropoff)]);
        if (p) setCoords(prev => ({ ...prev, pickup: [p.lat, p.lng] }));
        if (d) setCoords(prev => ({ ...prev, dropoff: [d.lat, d.lng] }));
        setLoading(false);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const [p, d] = await Promise.all([geocode(pickup), geocode(dropoff)]);
            if (!p || !d) {
                setError('Could not locate one or both addresses. Please be more specific.');
                setLoading(false);
                return;
            }

            const payload = {
                pickupAddress: pickup,
                pickupLat: p.lat,
                pickupLng: p.lng,
                dropoffAddress: dropoff,
                dropoffLat: d.lat,
                dropoffLng: d.lng,
                offeredPrice: fare ? parseFloat(fare) : null,
                vehicleType: vehicle,
                notes: notes.trim() || null
            };

            const response = await rideService.createRide(payload);
            navigate(`/rides/${response.data.id}`);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create ride request');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col lg:flex-row h-[calc(100vh-64px)] overflow-hidden">
            {/* Sidebar Form */}
            <div className="w-full lg:w-96 bg-white shadow-xl z-10 overflow-y-auto p-6 flex flex-col">
                <div className="mb-6">
                    <h1 className="text-2xl font-bold text-slate-900 mb-1">Request a Ride</h1>
                    <p className="text-slate-500 text-sm">Real-time driver bidding & tracking</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Pickup Location</label>
                        <div className="relative">
                            <input
                                type="text"
                                value={pickup}
                                onChange={(e) => setPickup(e.target.value)}
                                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-red-500 focus:border-red-500 transition-all outline-none"
                                placeholder="Enter pickup address"
                                required
                            />
                            <span className="absolute left-3 top-3.5 text-green-500 text-lg">📍</span>
                            {geoLoading && <div className="absolute right-3 top-3.5 animate-spin">⌛</div>}
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Dropoff Location</label>
                        <div className="relative">
                            <input
                                type="text"
                                value={dropoff}
                                onChange={(e) => setDropoff(e.target.value)}
                                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-red-500 focus:border-red-500 transition-all outline-none"
                                placeholder="Where are you going?"
                                onBlur={handlePreviewRoute}
                                required
                            />
                            <span className="absolute left-3 top-3.5 text-red-500 text-lg">🚩</span>
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Offer Your Fare (Optional)</label>
                        <div className="relative">
                            <input
                                type="number"
                                value={fare}
                                onChange={(e) => setFare(e.target.value)}
                                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-red-500 focus:border-red-500 transition-all outline-none"
                                placeholder="e.g. 500"
                            />
                            <span className="absolute left-3 top-3.5 text-slate-400 font-bold">$</span>
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Vehicle Type</label>
                        <div className="grid grid-cols-2 gap-3">
                            {VEHICLE_TYPES.map(type => (
                                <button
                                    key={type.value}
                                    type="button"
                                    onClick={() => setVehicle(type.value)}
                                    className={`
                                        p-3 text-left border rounded-xl transition-all
                                        ${vehicle === type.value 
                                            ? 'border-red-600 bg-red-50 ring-1 ring-red-600' 
                                            : 'border-slate-200 bg-white hover:border-slate-300'}
                                    `}
                                >
                                    <span className="text-xl block mb-1">{type.icon}</span>
                                    <span className={`text-xs font-bold block ${vehicle === type.value ? 'text-red-700' : 'text-slate-700'}`}>{type.label}</span>
                                    <span className="text-[10px] text-slate-400 line-clamp-1">{type.desc}</span>
                                </button>
                            ))}
                        </div>
                    </div>

                    {error && (
                        <div className="p-3 bg-red-50 border border-red-100 text-red-600 text-xs rounded-lg animate-shake">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full py-4 bg-red-600 text-white font-bold rounded-xl shadow-lg shadow-red-200 hover:bg-red-700 active:scale-95 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Finding Drivers...' : 'Request Ride'}
                    </button>
                </form>
            </div>

            {/* Map Section */}
            <div className="flex-1 relative bg-slate-100">
                <LiveTrackingMap
                    token={token}
                    pickupCoords={coords.pickup}
                    dropoffCoords={coords.dropoff}
                    initialDrivers={nearbyDrivers}
                />
                
                {(!coords.pickup && !geoLoading) && (
                    <div className="absolute inset-0 flex items-center justify-center bg-white/20 backdrop-blur-sm z-[1000]">
                        <div className="bg-white p-6 rounded-2xl shadow-xl border border-slate-100 max-w-xs text-center">
                            <div className="text-3xl mb-3">🌍</div>
                            <h3 className="font-bold text-slate-900 mb-2">Enable Location</h3>
                            <p className="text-slate-500 text-sm mb-4">Allow map access to see drivers around you and set your pickup point.</p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
