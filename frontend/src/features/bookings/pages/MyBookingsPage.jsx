import { useState, useEffect } from 'react';
import { bookingService } from '../services/bookingService';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import EmptyState from '../../../shared/components/EmptyState';

export default function MyBookingsPage() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState('all');

    useEffect(() => {
        fetchBookings();
    }, [filter]);

    const fetchBookings = async () => {
        try {
            setLoading(true);
            const response = filter === 'all'
                ? await bookingService.getUserBookings()
                : await bookingService.getUserBookingsByStatus(filter);

            setBookings(response.data.content || []);
        } catch (error) {
            console.error('Error fetching bookings:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCancelBooking = async (id) => {
        if (!confirm('Are you sure you want to cancel this booking?')) return;

        try {
            await bookingService.cancelBooking(id);
            fetchBookings();
        } catch (error) {
            alert('Failed to cancel booking');
        }
    };

    const getStatusBadge = (status) => {
        const badges = {
            REQUESTED: 'bg-yellow-100 text-yellow-800',
            CONFIRMED: 'bg-green-100 text-green-800',
            CANCELLED: 'bg-red-100 text-red-800',
            COMPLETED: 'bg-blue-100 text-blue-800',
        };
        return badges[status] || 'bg-gray-100 text-gray-800';
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">My Bookings</h1>

                {/* Filter Tabs */}
                <div className="flex gap-2 mb-6 overflow-x-auto">
                    {['all', 'REQUESTED', 'CONFIRMED', 'CANCELLED', 'COMPLETED'].map((status) => (
                        <button
                            key={status}
                            onClick={() => setFilter(status)}
                            className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${filter === status
                                    ? 'bg-[#EF4444] text-white'
                                    : 'bg-white text-gray-700 hover:bg-gray-100'
                                }`}
                        >
                            {status === 'all' ? 'All' : status.charAt(0) + status.slice(1).toLowerCase()}
                        </button>
                    ))}
                </div>

                {bookings.length === 0 ? (
                    <EmptyState
                        icon={
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                        }
                        title="No bookings found"
                        description="You haven't made any bookings yet"
                    />
                ) : (
                    <div className="space-y-4">
                        {bookings.map((booking) => (
                            <div key={booking.id} className="bg-white rounded-lg shadow-md p-6">
                                <div className="flex justify-between items-start mb-4">
                                    <div className="flex-1">
                                        <h3 className="text-xl font-semibold text-gray-900 mb-2">
                                            {booking.listingTitle}
                                        </h3>
                                        <div className="flex gap-4 text-sm text-gray-600">
                                            <span>📅 {booking.startDate} to {booking.endDate}</span>
                                            <span>💰 ${booking.totalAmount}</span>
                                        </div>
                                    </div>
                                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${getStatusBadge(booking.status)}`}>
                                        {booking.status}
                                    </span>
                                </div>

                                {booking.status === 'REQUESTED' && (
                                    <button
                                        onClick={() => handleCancelBooking(booking.id)}
                                        className="btn-outline text-red-600 border-red-600 hover:bg-red-50"
                                    >
                                        Cancel Booking
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
