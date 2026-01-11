import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { listingService } from '../services/listingService';
import ListingCard from '../../../shared/components/ListingCard';
import SearchBar from '../../../shared/components/SearchBar';

export default function HomePage() {
    const navigate = useNavigate();
    const [featuredListings, setFeaturedListings] = useState([]);
    const [stats, setStats] = useState({ total: 0, forSale: 0, forRent: 0 });

    useEffect(() => {
        fetchFeaturedListings();
    }, []);

    const fetchFeaturedListings = async () => {
        try {
            const response = await listingService.searchListings({ page: 0, size: 6 });
            setFeaturedListings(response.data.content || []);
            setStats({
                total: response.data.totalElements || 0,
                forSale: Math.floor((response.data.totalElements || 0) * 0.6),
                forRent: Math.floor((response.data.totalElements || 0) * 0.4),
            });
        } catch (error) {
            console.error('Error fetching featured listings:', error);
        }
    };

    const handleSearch = (query) => {
        navigate(`/listings?search=${encodeURIComponent(query)}`);
    };

    return (
        <div className="min-h-screen">
            {/* Hero Section */}
            <section className="hero-gradient text-white py-20 relative">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
                    <div className="text-center animate-fadeIn">
                        <h1 className="text-5xl md:text-6xl font-black mb-6">
                            Find Your <span className="gradient-text">Dream Car</span>
                        </h1>
                        <p className="text-xl md:text-2xl text-gray-300 mb-8 max-w-3xl mx-auto">
                            Buy, sell, or rent premium vehicles with confidence. Your perfect ride is just a click away.
                        </p>

                        {/* Search Bar */}
                        <div className="max-w-2xl mx-auto mb-8">
                            <SearchBar onSearch={handleSearch} placeholder="Search by make, model, or keyword..." />
                        </div>

                        {/* Quick Actions */}
                        <div className="flex flex-wrap justify-center gap-4">
                            <button onClick={() => navigate('/listings')} className="btn btn-primary">
                                Browse All Cars
                            </button>
                            <button onClick={() => navigate('/rentals')} className="btn btn-outline">
                                Explore Rentals
                            </button>
                        </div>
                    </div>

                    {/* Stats */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-16">
                        <div className="glass rounded-xl p-6 text-center animate-slideInLeft">
                            <div className="text-4xl font-bold text-[#EF4444] mb-2">{stats.total}+</div>
                            <div className="text-gray-300">Total Vehicles</div>
                        </div>
                        <div className="glass rounded-xl p-6 text-center animate-fadeIn" style={{ animationDelay: '0.1s' }}>
                            <div className="text-4xl font-bold text-[#FBBF24] mb-2">{stats.forSale}+</div>
                            <div className="text-gray-300">For Sale</div>
                        </div>
                        <div className="glass rounded-xl p-6 text-center animate-slideInRight">
                            <div className="text-4xl font-bold text-[#10B981] mb-2">{stats.forRent}+</div>
                            <div className="text-gray-300">For Rent</div>
                        </div>
                    </div>
                </div>

                {/* Decorative Elements */}
                <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
                    <div className="absolute top-20 left-10 w-72 h-72 bg-[#EF4444] rounded-full mix-blend-multiply filter blur-3xl opacity-10 animate-pulse"></div>
                    <div className="absolute bottom-20 right-10 w-72 h-72 bg-[#FBBF24] rounded-full mix-blend-multiply filter blur-3xl opacity-10 animate-pulse" style={{ animationDelay: '1s' }}></div>
                </div>
            </section>

            {/* Features Section */}
            <section className="py-16 bg-white">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-4xl font-bold text-gray-900 mb-4">Why Choose Us?</h2>
                        <p className="text-xl text-gray-600">Experience the best car marketplace platform</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {/* Feature 1 */}
                        <div className="card hover-lift text-center">
                            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-[#EF4444]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">Verified Listings</h3>
                            <p className="text-gray-600">All vehicles are verified by our team for quality and authenticity</p>
                        </div>

                        {/* Feature 2 */}
                        <div className="card hover-lift text-center">
                            <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-[#FBBF24]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">Best Prices</h3>
                            <p className="text-gray-600">Competitive pricing with flexible payment and rental options</p>
                        </div>

                        {/* Feature 3 */}
                        <div className="card hover-lift text-center">
                            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="w-8 h-8 text-[#10B981]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                                </svg>
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">Quick Process</h3>
                            <p className="text-gray-600">Fast and easy booking process with instant confirmation</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Featured Listings */}
            <section className="py-16 bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center mb-8">
                        <div>
                            <h2 className="text-3xl font-bold text-gray-900 mb-2">Featured Vehicles</h2>
                            <p className="text-gray-600">Handpicked premium cars just for you</p>
                        </div>
                        <button onClick={() => navigate('/listings')} className="btn btn-outline">
                            View All
                        </button>
                    </div>

                    {featuredListings.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {featuredListings.map((listing) => (
                                <div key={listing.id} className="animate-fadeIn">
                                    <ListingCard
                                        listing={listing}
                                        onClick={() => navigate(`/listings/${listing.slug}`)}
                                    />
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-12">
                            <p className="text-gray-500">No featured listings available at the moment</p>
                        </div>
                    )}
                </div>
            </section>

            {/* How It Works */}
            <section className="py-16 bg-white">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-4xl font-bold text-gray-900 mb-4">How It Works</h2>
                        <p className="text-xl text-gray-600">Get started in three simple steps</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {/* Step 1 */}
                        <div className="relative">
                            <div className="flex flex-col items-center text-center">
                                <div className="w-20 h-20 bg-gradient-to-br from-[#EF4444] to-[#DC2626] rounded-full flex items-center justify-center text-white text-2xl font-bold mb-4 shadow-lg">
                                    1
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">Browse & Search</h3>
                                <p className="text-gray-600">Explore thousands of verified vehicles with advanced filters</p>
                            </div>
                            {/* Arrow */}
                            <div className="hidden md:block absolute top-10 left-full w-full">
                                <svg className="w-full h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 100 20">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 10 L95 10 M85 5 L95 10 L85 15" />
                                </svg>
                            </div>
                        </div>

                        {/* Step 2 */}
                        <div className="relative">
                            <div className="flex flex-col items-center text-center">
                                <div className="w-20 h-20 bg-gradient-to-br from-[#FBBF24] to-[#F59E0B] rounded-full flex items-center justify-center text-white text-2xl font-bold mb-4 shadow-lg">
                                    2
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">Book or Buy</h3>
                                <p className="text-gray-600">Choose to purchase or rent with flexible payment options</p>
                            </div>
                            {/* Arrow */}
                            <div className="hidden md:block absolute top-10 left-full w-full">
                                <svg className="w-full h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 100 20">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 10 L95 10 M85 5 L95 10 L85 15" />
                                </svg>
                            </div>
                        </div>

                        {/* Step 3 */}
                        <div className="flex flex-col items-center text-center">
                            <div className="w-20 h-20 bg-gradient-to-br from-[#10B981] to-[#059669] rounded-full flex items-center justify-center text-white text-2xl font-bold mb-4 shadow-lg">
                                3
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">Drive Away</h3>
                            <p className="text-gray-600">Complete the process and enjoy your new ride!</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="py-16 bg-gradient-to-r from-[#DC2626] to-[#B91C1C] text-white">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
                    <h2 className="text-4xl font-bold mb-4">Ready to Find Your Perfect Car?</h2>
                    <p className="text-xl mb-8 text-gray-100">Join thousands of satisfied customers today</p>
                    <div className="flex flex-wrap justify-center gap-4">
                        <button onClick={() => navigate('/register')} className="btn bg-white text-[#DC2626] hover:bg-gray-100">
                            Get Started Free
                        </button>
                        <button onClick={() => navigate('/listings')} className="btn btn-outline border-white text-white hover:bg-white hover:text-[#DC2626]">
                            Browse Vehicles
                        </button>
                    </div>
                </div>
            </section>
        </div>
    );
}
