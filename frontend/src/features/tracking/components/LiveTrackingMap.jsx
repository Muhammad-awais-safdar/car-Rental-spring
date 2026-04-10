import { useEffect, useState, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Circle, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useWebSocket } from '../../../core/hooks/useWebSocket';

// Fix default marker icon paths broken by Vite bundler
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

/** Custom car icon for drivers */
const driverIcon = (heading = 0) => L.divIcon({
    className: '',
    html: `<div style="
        width:36px; height:36px;
        background: linear-gradient(135deg,#f59e0b,#ea580c);
        border-radius:50%; border:3px solid #fff;
        display:flex; align-items:center; justify-content:center;
        box-shadow:0 4px 12px rgba(245,158,11,.5);
        transform:rotate(${heading}deg);
        transition:transform .5s ease;
    ">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
            <path d="M12 2L8 10H4l4 4-2 6 6-3 6 3-2-6 4-4h-4L12 2z"/>
        </svg>
    </div>`,
    iconSize: [36, 36],
    iconAnchor: [18, 18],
    popupAnchor: [0, -20],
});

/** Pickup pin icon */
const pickupIcon = L.divIcon({
    className: '',
    html: `<div style="width:18px;height:18px;background:#22c55e;border-radius:50%;border:3px solid #fff;box-shadow:0 2px 8px rgba(34,197,94,.5)"></div>`,
    iconSize: [18, 18],
    iconAnchor: [9, 9],
});

/** Dropoff pin icon */
const dropoffIcon = L.divIcon({
    className: '',
    html: `<div style="width:18px;height:18px;background:#ef4444;border-radius:50%;border:3px solid #fff;box-shadow:0 2px 8px rgba(239,68,68,.5)"></div>`,
    iconSize: [18, 18],
    iconAnchor: [9, 9],
});

/** Smoothly fly to new coordinates when the center prop changes */
function MapController({ center }) {
    const map = useMap();
    useEffect(() => {
        if (center) map.flyTo(center, map.getZoom(), { animate: true, duration: 1 });
    }, [center, map]);
    return null;
}

/**
 * LiveTrackingMap - Real-time driver position map for passengers.
 *
 * Props:
 *   token        {string}   JWT for WebSocket auth
 *   driverId     {number}   assigned driver's DB id
 *   initialDriverLocation {[lat,lng]} optional initial coords
 *   pickupCoords  {[lat,lng]}
 *   dropoffCoords {[lat,lng]}
 *   initialDrivers {Array}  list of OnlineDriverSnapshot (for ride request screen)
 */
export default function LiveTrackingMap({
    token,
    driverId,
    initialDriverLocation,
    pickupCoords,
    dropoffCoords,
    initialDrivers = [],
}) {
    const [driverPos, setDriverPos] = useState(
        initialDriverLocation ?? (pickupCoords || [31.5204, 74.3587])
    );
    const [driverInfo, setDriverInfo] = useState(null);
    const [nearbyDrivers, setNearbyDrivers] = useState(initialDrivers);

    // Default map center: pickup, or Lahore if nothing provided
    const defaultCenter = pickupCoords ?? [31.5204, 74.3587];

    const { subscribe } = useWebSocket(token);

    // Subscribe to the specific assigned driver's location topic
    useEffect(() => {
        if (!driverId) return;
        const unsub = subscribe(`/topic/driver/${driverId}/location`, (data) => {
            setDriverPos([parseFloat(data.latitude), parseFloat(data.longitude)]);
            setDriverInfo(data);
        });
        return unsub;
    }, [driverId, subscribe]);

    return (
        <div className="w-full h-full rounded-2xl overflow-hidden relative">
            <MapContainer
                center={defaultCenter}
                zoom={14}
                style={{ width: '100%', height: '100%' }}
                className="z-0"
            >
                {/* Dark-mode tile layer (CartoDB Dark Matter) */}
                <TileLayer
                    url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
                    attribution='&copy; <a href="https://carto.com/">CARTO</a>'
                />

                {/* Fly camera to driver as it moves */}
                {driverId && driverInfo && (
                    <MapController center={[parseFloat(driverInfo.latitude), parseFloat(driverInfo.longitude)]} />
                )}

                {/* Assigned driver marker */}
                {driverId && (
                    <Marker
                        position={driverPos}
                        icon={driverIcon(driverInfo?.heading ?? 0)}
                    >
                        <Popup className="dark-popup">
                            <div style={{ minWidth: 140 }}>
                                <p style={{ fontWeight: 700, color: '#f59e0b' }}>
                                    {driverInfo?.driverName ?? 'Your Driver'}
                                </p>
                                {driverInfo?.speedKmh != null && (
                                    <p style={{ color: '#94a3b8', fontSize: 12, marginTop: 4 }}>
                                        {Math.round(driverInfo.speedKmh)} km/h
                                    </p>
                                )}
                            </div>
                        </Popup>
                    </Marker>
                )}

                {/* Pickup pin */}
                {pickupCoords && (
                    <Marker position={pickupCoords} icon={pickupIcon}>
                        <Popup><span style={{ color: '#22c55e', fontWeight: 700 }}>Pickup</span></Popup>
                    </Marker>
                )}

                {/* Dropoff pin */}
                {dropoffCoords && (
                    <Marker position={dropoffCoords} icon={dropoffIcon}>
                        <Popup><span style={{ color: '#ef4444', fontWeight: 700 }}>Dropoff</span></Popup>
                    </Marker>
                )}

                {/* Nearby online drivers (ride-request screen) */}
                {!driverId && nearbyDrivers.map(d => (
                    <Marker
                        key={d.driverId}
                        position={[parseFloat(d.latitude), parseFloat(d.longitude)]}
                        icon={driverIcon(d.heading ?? 0)}
                    >
                        <Popup>
                            <div style={{ minWidth: 140 }}>
                                <p style={{ fontWeight: 700 }}>{d.driverName}</p>
                                <p style={{ color: '#f59e0b', fontSize: 12 }}>★ {d.driverRating}</p>
                                <p style={{ color: '#94a3b8', fontSize: 12 }}>{d.driverVehicleType}</p>
                            </div>
                        </Popup>
                    </Marker>
                ))}

                {/* Search radius circle on ride-request screen */}
                {pickupCoords && !driverId && (
                    <Circle
                        center={pickupCoords}
                        radius={5000}
                        pathOptions={{ color: '#f59e0b', fillColor: '#f59e0b', fillOpacity: 0.05, weight: 1 }}
                    />
                )}
            </MapContainer>

            {/* Connection status badge */}
            {driverId && (
                <div className="absolute top-3 right-3 z-[1000] bg-slate-900/90 backdrop-blur-sm border border-slate-700 rounded-full px-3 py-1 flex items-center gap-2">
                    <span className={`w-2 h-2 rounded-full ${driverInfo ? 'bg-green-400 animate-pulse' : 'bg-slate-500'}`}></span>
                    <span className="text-xs text-slate-300">
                        {driverInfo ? 'Live' : 'Waiting for driver...'}
                    </span>
                </div>
            )}
        </div>
    );
}
