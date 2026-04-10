import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WS_URL = import.meta.env.VITE_API_URL
    ? import.meta.env.VITE_API_URL.replace('/api', '/ws')
    : 'http://localhost:8080/ws';

/**
 * Custom hook that manages the STOMP WebSocket connection lifecycle.
 *
 * Usage:
 *   const { subscribe, publish, disconnect } = useWebSocket(token);
 */
export function useWebSocket(token) {
    const clientRef = useRef(null);
    const subscriptionsRef = useRef(new Map());

    const connect = useCallback(() => {
        if (clientRef.current?.active) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            connectHeaders: {
                Authorization: token ? `Bearer ${token}` : '',
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,

            onConnect: () => {
                console.log('[WS] Connected');
                // Re-subscribe any queued subscriptions after reconnect
                subscriptionsRef.current.forEach(({ destination, callback }) => {
                    client.subscribe(destination, (msg) => callback(JSON.parse(msg.body)));
                });
            },

            onDisconnect: () => console.log('[WS] Disconnected'),

            onStompError: (frame) =>
                console.error('[WS] STOMP error', frame),
        });

        client.activate();
        clientRef.current = client;
    }, [token]);

    /**
     * Subscribe to a STOMP topic.
     * @param {string} destination  e.g. '/topic/driver/42/location'
     * @param {function} callback   called with parsed JSON body
     * @returns {function}          unsubscribe function
     */
    const subscribe = useCallback((destination, callback) => {
        const id = destination + Date.now();
        subscriptionsRef.current.set(id, { destination, callback });

        if (clientRef.current?.active) {
            const sub = clientRef.current.subscribe(destination, (msg) =>
                callback(JSON.parse(msg.body)));
            return () => {
                sub.unsubscribe();
                subscriptionsRef.current.delete(id);
            };
        }
        // If not yet connected, it will be subscribed on onConnect
        return () => subscriptionsRef.current.delete(id);
    }, []);

    /**
     * Publish a message to the server.
     * @param {string} destination  e.g. '/app/driver/location'
     * @param {object} body         will be JSON.stringified
     */
    const publish = useCallback((destination, body) => {
        if (clientRef.current?.active) {
            clientRef.current.publish({
                destination,
                body: JSON.stringify(body),
            });
        }
    }, []);

    const disconnect = useCallback(() => {
        clientRef.current?.deactivate();
    }, []);

    useEffect(() => {
        if (token) connect();
        return () => disconnect();
    }, [token, connect, disconnect]);

    return { subscribe, publish, disconnect };
}
