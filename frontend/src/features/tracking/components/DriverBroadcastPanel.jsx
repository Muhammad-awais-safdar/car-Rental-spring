import { useState, useEffect, useRef } from 'react';
import { useDriverTracking } from '../../../core/hooks/useDriverTracking';

/**
 * DriverBroadcastPanel
 * This component handles the driver's availability and real-time location broadcasting.
 */
export default function DriverBroadcastPanel({ token }) {
    const [isOnline, setIsOnline] = useState(false);
    const [lastLoc, setLastLoc] = useState(null);
    const [error, setError] = useState(null);
    
    const { publishLocation, goOffline, isConnected } = useDriverTracking(token);
    const watchIdRef = useRef(null);

    const toggleOnline = () => {
        if (isOnline) {
            stopTracking();
        } else {
            startTracking();
        }
    };

    const startTracking = () => {
        if (!navigator.geolocation) {
            setError('Geolocation is not supported by your browser');
            return;
        }

        setIsOnline(true);
        setError(null);

        watchIdRef.current = navigator.geolocation.watchPosition(
            (pos) => {
                const { latitude, longitude, heading, speed, accuracy } = pos.coords;
                setLastLoc({ lat: latitude, lng: longitude });
                
                // Publish to WebSocket
                publishLocation(latitude, longitude, heading, speed, accuracy);
            },
            (err) => {
                setError(`Geolocation error: ${err.message}`);
                setIsOnline(false);
            },
            {
                enableHighAccuracy: true,
                maximumAge: 1000,
                timeout: 5000
            }
        );
    };

    const stopTracking = () => {
        if (watchIdRef.current) {
            navigator.geolocation.clearWatch(watchIdRef.current);
            watchIdRef.current = null;
        }
        goOffline();
        setIsOnline(false);
        setLastLoc(null);
    };

    // Cleanup on unmount
    useEffect(() => {
        return () => {
            if (watchIdRef.current) {
                navigator.geolocation.clearWatch(watchIdRef.current);
            }
        };
    }, []);

    return (
        <div className="bg-slate-900 text-white rounded-2xl p-6 shadow-2xl border border-slate-800">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h2 className="text-xl font-bold flex items-center gap-2">
                        <span className="text-2xl text-red-500">🛰️</span>
                        Driver Availability
                    </h2>
                    <p className="text-slate-400 text-sm">Broadcast your location to receive ride requests</p>
                </div>
                <div 
                    onClick={toggleOnline}
                    className={`
                        w-16 h-8 rounded-full p-1 cursor-pointer transition-all duration-300
                        ${isOnline ? 'bg-green-500' : 'bg-slate-700'}
                    `}
                >
                    <div className={`
                        w-6 h-6 bg-white rounded-full shadow-md transform transition-transform duration-300
                        ${isOnline ? 'translate-x-8' : 'translate-x-0'}
                    `} />
                </div>
            </div>

            {error && (
                <div className="bg-red-500/10 border border-red-500/20 text-red-400 p-3 rounded-lg text-sm mb-4">
                    {error}
                </div>
            )}

            <div className="grid grid-cols-2 gap-4">
                <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/50">
                    <div className="text-slate-400 text-xs uppercase font-bold tracking-wider mb-1">Status</div>
                    <div className="flex items-center gap-2">
                        <div className={`w-2 h-2 rounded-full ${isOnline ? 'bg-green-500 animate-pulse' : 'bg-slate-500'}`} />
                        <span className={isOnline ? 'text-green-400 font-medium' : 'text-slate-400'}>
                            {isOnline ? 'Online' : 'Offline'}
                        </span>
                    </div>
                </div>
                <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700/50">
                    <div className="text-slate-400 text-xs uppercase font-bold tracking-wider mb-1">Connection</div>
                    <div className="flex items-center gap-2">
                        <div className={`w-2 h-2 rounded-full ${isConnected.current ? 'bg-blue-500' : 'bg-red-500'}`} />
                        <span className={isConnected.current ? 'text-blue-400 font-medium' : 'text-red-400'}>
                            {isConnected.current ? 'Linked' : 'Disconnected'}
                        </span>
                    </div>
                </div>
            </div>

            {isOnline && lastLoc && (
                <div className="mt-4 p-4 bg-slate-800/30 rounded-xl border border-dashed border-slate-700">
                    <div className="flex justify-between items-center text-xs text-slate-500 mb-2">
                        <span>Current Coordinates</span>
                        <span className="bg-green-500/20 text-green-400 px-2 py-0.5 rounded">Active</span>
                    </div>
                    <div className="flex justify-between font-mono text-sm text-slate-300">
                        <span>Lat: {lastLoc.lat.toFixed(6)}</span>
                        <span>Lng: {lastLoc.lng.toFixed(6)}</span>
                    </div>
                </div>
            )}
        </div>
    );
}
