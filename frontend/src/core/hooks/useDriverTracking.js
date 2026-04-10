import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WS_URL = import.meta.env.VITE_API_URL
    ? import.meta.env.VITE_API_URL.replace('/api', '/ws')
    : 'http://localhost:8080/ws';

/**
 * Custom hook for a DRIVER to publish their location via WebSocket.
 *
 * Usage:
 *   const { isConnected, publishLocation, goOffline } = useDriverTracking(token);
 */
export function useDriverTracking(token) {
    const clientRef = useRef(null);
    const isConnectedRef = useRef(false);

    const connect = useCallback(() => {
        if (!token || clientRef.current?.active) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            connectHeaders: { Authorization: `Bearer ${token}` },
            reconnectDelay: 5000,
            onConnect: () => {
                isConnectedRef.current = true;
                console.log('[DriverTracking] Connected');
            },
            onDisconnect: () => {
                isConnectedRef.current = false;
            },
        });

        client.activate();
        clientRef.current = client;
    }, [token]);

    /** Send a location payload to the server */
    const publishLocation = useCallback((latitude, longitude, heading, speedKmh, accuracyM) => {
        if (!clientRef.current?.active) return;
        clientRef.current.publish({
            destination: '/app/update-location',
            body: JSON.stringify({ latitude, longitude, heading, speedKmh, accuracyM }),
        });
    }, []);

    /** Tell the server this driver is going offline */
    const goOffline = useCallback(() => {
        if (!clientRef.current?.active) return;
        clientRef.current.publish({ destination: '/app/driver/offline', body: '{}' });
        clientRef.current.deactivate();
        isConnectedRef.current = false;
    }, []);

    useEffect(() => {
        connect();
        return () => {
            goOffline();
        };
    }, [token, connect, goOffline]);

    return { publishLocation, goOffline, isConnected: isConnectedRef };
}
