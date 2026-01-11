import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { listingService } from '../services/listingService';
import ListingCard from '../../../shared/components/ListingCard';
import Pagination from '../../../shared/components/Pagination';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import EmptyState from '../../../shared/components/EmptyState';

export default function ListingsPage() {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();

    const [listings, setListings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // Filters
    const [filters, setFilters] = useState({
        search: searchParams.get('search') || '',
        make: searchParams.get('make') || '',
        model: searchParams.get('model') || '',
        minPrice: searchParams.get('minPrice') || '',
        maxPrice: searchParams.get('maxPrice') || '',
        minYear: searchParams.get('minYear') || '',
        maxYear: searchParams.get('maxYear') || '',
        location: searchParams.get('location') || '',
        listingType: searchParams.get('listingType') || '',
    });

    useEffect(() => {
        fetchListings();
    }, [currentPage, searchParams]);

    const fetchListings = async () => {
        try {
            setLoading(true);
            const response = await listingService.searchListings({
                page: currentPage,
                size: 12,
                ...Object.fromEntries(
                    Object.entries(filters).filter(([_, v]) => v !== '')
                ),
            });

            setListings(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);
        } catch (error) {
            console.error('Error fetching listings:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value }));
    };

    const applyFilters = () => {
        const params = {};
        Object.entries(filters).forEach(([key, value]) => {
            if (value) params[key] = value;
        });
        setSearchParams(params);
        setCurrentPage(0);
    };

    const clearFilters = () => {
        setFilters({
            search: '',
            make: '',
            model: '',
            minPrice: '',
            maxPrice: '',
            minYear: '',
            maxYear: '',
            location: '',
            listingType: '',
        });
        setSearchParams({});
        setCurrentPage(0);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Browse Listings</h1>
                    <p className="text-gray-600">Find your perfect car from thousands of listings</p>
                </div>

                {/* Filters */}
                <div className="bg-white rounded-lg shadow-md p-6 mb-8">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Filters</h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        {/* Search */}
                        <input
                            type="text"
                            placeholder="Search..."
                            value={filters.search}
                            onChange={(e) => handleFilterChange('search', e.target.value)}
                            className="input-field"
                        />

                        {/* Make */}
                        <input
                            type="text"
                            placeholder="Make (e.g., Toyota)"
                            value={filters.make}
                            onChange={(e) => handleFilterChange('make', e.target.value)}
                            className="input-field"
                        />

                        {/* Model */}
                        <input
                            type="text"
                            placeholder="Model (e.g., Camry)"
                            value={filters.model}
                            onChange={(e) => handleFilterChange('model', e.target.value)}
                            className="input-field"
                        />

                        {/* Location */}
                        <input
                            type="text"
                            placeholder="Location"
                            value={filters.location}
                            onChange={(e) => handleFilterChange('location', e.target.value)}
                            className="input-field"
                        />

                        {/* Min Price */}
                        <input
                            type="number"
                            placeholder="Min Price"
                            value={filters.minPrice}
                            onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                            className="input-field"
                        />

                        {/* Max Price */}
                        <input
                            type="number"
                            placeholder="Max Price"
                            value={filters.maxPrice}
                            onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                            className="input-field"
                        />

                        {/* Min Year */}
                        <input
                            type="number"
                            placeholder="Min Year"
                            value={filters.minYear}
                            onChange={(e) => handleFilterChange('minYear', e.target.value)}
                            className="input-field"
                        />

                        {/* Max Year */}
                        <input
                            type="number"
                            placeholder="Max Year"
                            value={filters.maxYear}
                            onChange={(e) => handleFilterChange('maxYear', e.target.value)}
                            className="input-field"
                        />

                        {/* Listing Type */}
                        <select
                            value={filters.listingType}
                            onChange={(e) => handleFilterChange('listingType', e.target.value)}
                            className="input-field"
                        >
                            <option value="">All Types</option>
                            <option value="SELL">For Sale</option>
                            <option value="RENT">For Rent</option>
                        </select>
                    </div>

                    {/* Filter Actions */}
                    <div className="flex gap-4 mt-6">
                        <button onClick={applyFilters} className="btn-primary">
                            Apply Filters
                        </button>
                        <button onClick={clearFilters} className="btn-outline">
                            Clear Filters
                        </button>
                    </div>
                </div>

                {/* Results */}
                {loading ? (
                    <LoadingSpinner />
                ) : listings.length === 0 ? (
                    <EmptyState
                        icon={
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                        }
                        title="No listings found"
                        description="Try adjusting your filters or search criteria"
                    />
                ) : (
                    <>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {listings.map((listing) => (
                                <ListingCard
                                    key={listing.id}
                                    listing={listing}
                                    onClick={() => navigate(`/listings/${listing.slug}`)}
                                />
                            ))}
                        </div>

                        {totalPages > 1 && (
                            <Pagination
                                currentPage={currentPage}
                                totalPages={totalPages}
                                onPageChange={setCurrentPage}
                            />
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
