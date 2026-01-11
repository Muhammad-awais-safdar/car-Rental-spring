import { useState, useEffect } from 'react';
import { adminService } from '../services/adminService';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import EmptyState from '../../../shared/components/EmptyState';

export default function AdminDashboardPage() {
    const [pendingListings, setPendingListings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchPendingListings();
    }, []);

    const fetchPendingListings = async () => {
        try {
            const response = await adminService.getPendingListings({ size: 20 });
            setPendingListings(response.data.content || []);
        } catch (error) {
            console.error('Error fetching pending listings:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleApprove = async (id) => {
        try {
            await adminService.approveListing(id);
            setPendingListings(prev => prev.filter(l => l.id !== id));
            alert('Listing approved successfully!');
        } catch (error) {
            alert('Failed to approve listing');
        }
    };

    const handleReject = async (id) => {
        if (!confirm('Are you sure you want to reject this listing?')) return;

        try {
            await adminService.rejectListing(id);
            setPendingListings(prev => prev.filter(l => l.id !== id));
            alert('Listing rejected');
        } catch (error) {
            alert('Failed to reject listing');
        }
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Admin Dashboard</h1>
                    <p className="text-gray-600">Manage listings and moderate content</p>
                </div>

                {/* Stats Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-gray-600 text-sm">Pending Listings</p>
                                <p className="text-3xl font-bold text-[#EF4444]">{pendingListings.length}</p>
                            </div>
                            <div className="bg-yellow-100 p-3 rounded-full">
                                <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-gray-600 text-sm">Total Users</p>
                                <p className="text-3xl font-bold text-gray-900">-</p>
                            </div>
                            <div className="bg-blue-100 p-3 rounded-full">
                                <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                </svg>
                            </div>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-gray-600 text-sm">Active Bookings</p>
                                <p className="text-3xl font-bold text-gray-900">-</p>
                            </div>
                            <div className="bg-green-100 p-3 rounded-full">
                                <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                                </svg>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Pending Listings */}
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-xl font-semibold text-gray-900 mb-6">Pending Listings</h2>

                    {pendingListings.length === 0 ? (
                        <EmptyState
                            icon={
                                <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            }
                            title="No pending listings"
                            description="All listings have been reviewed"
                        />
                    ) : (
                        <div className="space-y-4">
                            {pendingListings.map((listing) => (
                                <div key={listing.id} className="border border-gray-200 rounded-lg p-4 hover:border-[#EF4444] transition-colors">
                                    <div className="flex justify-between items-start">
                                        <div className="flex-1">
                                            <h3 className="text-lg font-semibold text-gray-900 mb-2">{listing.title}</h3>
                                            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-gray-600">
                                                <div>
                                                    <span className="font-medium">Make:</span> {listing.make}
                                                </div>
                                                <div>
                                                    <span className="font-medium">Model:</span> {listing.model}
                                                </div>
                                                <div>
                                                    <span className="font-medium">Year:</span> {listing.year}
                                                </div>
                                                <div>
                                                    <span className="font-medium">Price:</span> ${listing.price?.toLocaleString()}
                                                </div>
                                            </div>
                                            <p className="text-gray-700 mt-2 line-clamp-2">{listing.description}</p>
                                        </div>
                                    </div>

                                    <div className="flex gap-3 mt-4">
                                        <button
                                            onClick={() => handleApprove(listing.id)}
                                            className="btn-primary"
                                        >
                                            ✓ Approve
                                        </button>
                                        <button
                                            onClick={() => handleReject(listing.id)}
                                            className="btn-outline text-red-600 border-red-600 hover:bg-red-50"
                                        >
                                            ✗ Reject
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
