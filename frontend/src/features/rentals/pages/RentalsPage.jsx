import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { listingService } from '../../listings/services/listingService';
import ListingCard from '../../../shared/components/ListingCard';
import Pagination from '../../../shared/components/Pagination';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';
import EmptyState from '../../../shared/components/EmptyState';

export default function RentalsPage() {
    const navigate = useNavigate();
    const [rentals, setRentals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [filters, setFilters] = useState({
        search: '',
        make: '',
        model: '',
        minPrice: '',
        maxPrice: '',
        location: '',
    });

    useEffect(() => {
        fetchRentals();
    }, [currentPage]);

    const fetchRentals = async () => {
        try {
            setLoading(true);
            const response = await listingService.searchListings({
                page: currentPage,
                size: 12,
                listingType: 'RENT',
                ...Object.fromEntries(
                    Object.entries(filters).filter(([_, v]) => v !== '')
                ),
            });

            setRentals(response.data.content || []);
            setTotalPages(response.data.totalPages || 0);
        } catch (error) {
            console.error('Error fetching rentals:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value }));
    };

    const applyFilters = () => {
        setCurrentPage(0);
        fetchRentals();
    };

    const clearFilters = () => {
        setFilters({
            search: '',
            make: '',
            model: '',
            minPrice: '',
            maxPrice: '',
            location: '',
        });
        setCurrentPage(0);
        setTimeout(fetchRentals, 100);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">Browse Rental Cars</h1>
                    <p className="text-gray-600">Find the perfect car to rent for your needs</p>
                </div>

                {/* Filters */}
                <div className="bg-white rounded-lg shadow-md p-6 mb-8">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Filters</h2>

                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        <input
                            type="text"
                            placeholder="Search..."
                            value={filters.search}
                            onChange={(e) => handleFilterChange('search', e.target.value)}
                            className="input-field"
                        />

                        <input
                            type="text"
                            placeholder="Make (e.g., Toyota)"
                            value={filters.make}
                            onChange={(e) => handleFilterChange('make', e.target.value)}
                            className="input-field"
                        />

                        <input
                            type="text"
                            placeholder="Model (e.g., Camry)"
                            value={filters.model}
                            onChange={(e) => handleFilterChange('model', e.target.value)}
                            className="input-field"
                        />

                        <input
                            type="text"
                            placeholder="Location"
                            value={filters.location}
                            onChange={(e) => handleFilterChange('location', e.target.value)}
                            className="input-field"
                        />

                        <input
                            type="number"
                            placeholder="Min Daily Rate"
                            value={filters.minPrice}
                            onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                            className="input-field"
                        />

                        <input
                            type="number"
                            placeholder="Max Daily Rate"
                            value={filters.maxPrice}
                            onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                            className="input-field"
                        />
                    </div>

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
                ) : rentals.length === 0 ? (
                    <EmptyState
                        icon={
                            <svg className="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                        }
                        title="No rental cars found"
                        description="Try adjusting your filters or check back later"
                    />
                ) : (
                    <>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                            {rentals.map((rental) => (
                                <ListingCard
                                    key={rental.id}
                                    listing={rental}
                                    onClick={() => navigate(`/rentals/${rental.slug}`)}
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
