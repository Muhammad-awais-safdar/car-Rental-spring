import { useState } from 'react';

export default function FilterSidebar({ onFilterChange, initialFilters = {} }) {
    const [isOpen, setIsOpen] = useState(true);
    const [filters, setFilters] = useState({
        make: initialFilters.make || '',
        model: initialFilters.model || '',
        minPrice: initialFilters.minPrice || '',
        maxPrice: initialFilters.maxPrice || '',
        minYear: initialFilters.minYear || '',
        maxYear: initialFilters.maxYear || '',
        location: initialFilters.location || '',
        condition: initialFilters.condition || '',
        listingType: initialFilters.listingType || '',
    });

    const handleChange = (key, value) => {
        const newFilters = { ...filters, [key]: value };
        setFilters(newFilters);
    };

    const applyFilters = () => {
        if (onFilterChange) {
            onFilterChange(filters);
        }
    };

    const clearFilters = () => {
        const emptyFilters = {
            make: '',
            model: '',
            minPrice: '',
            maxPrice: '',
            minYear: '',
            maxYear: '',
            location: '',
            condition: '',
            listingType: '',
        };
        setFilters(emptyFilters);
        if (onFilterChange) {
            onFilterChange(emptyFilters);
        }
    };

    return (
        <div className="bg-white rounded-lg shadow-md">
            {/* Header */}
            <div className="flex justify-between items-center p-4 border-b">
                <h3 className="text-lg font-semibold text-gray-900">Filters</h3>
                <button
                    onClick={() => setIsOpen(!isOpen)}
                    className="lg:hidden p-2 hover:bg-gray-100 rounded-full"
                >
                    <svg
                        className={`w-5 h-5 transition-transform ${isOpen ? 'rotate-180' : ''}`}
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                    </svg>
                </button>
            </div>

            {/* Filter Content */}
            <div className={`p-4 space-y-4 ${isOpen ? 'block' : 'hidden lg:block'}`}>
                {/* Listing Type */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Listing Type</label>
                    <select
                        value={filters.listingType}
                        onChange={(e) => handleChange('listingType', e.target.value)}
                        className="input-field"
                    >
                        <option value="">All Types</option>
                        <option value="SELL">For Sale</option>
                        <option value="RENT">For Rent</option>
                    </select>
                </div>

                {/* Make */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Make</label>
                    <input
                        type="text"
                        value={filters.make}
                        onChange={(e) => handleChange('make', e.target.value)}
                        placeholder="e.g., Toyota"
                        className="input-field"
                    />
                </div>

                {/* Model */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Model</label>
                    <input
                        type="text"
                        value={filters.model}
                        onChange={(e) => handleChange('model', e.target.value)}
                        placeholder="e.g., Camry"
                        className="input-field"
                    />
                </div>

                {/* Price Range */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Price Range</label>
                    <div className="grid grid-cols-2 gap-2">
                        <input
                            type="number"
                            value={filters.minPrice}
                            onChange={(e) => handleChange('minPrice', e.target.value)}
                            placeholder="Min"
                            className="input-field"
                        />
                        <input
                            type="number"
                            value={filters.maxPrice}
                            onChange={(e) => handleChange('maxPrice', e.target.value)}
                            placeholder="Max"
                            className="input-field"
                        />
                    </div>
                </div>

                {/* Year Range */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Year Range</label>
                    <div className="grid grid-cols-2 gap-2">
                        <input
                            type="number"
                            value={filters.minYear}
                            onChange={(e) => handleChange('minYear', e.target.value)}
                            placeholder="Min"
                            className="input-field"
                        />
                        <input
                            type="number"
                            value={filters.maxYear}
                            onChange={(e) => handleChange('maxYear', e.target.value)}
                            placeholder="Max"
                            className="input-field"
                        />
                    </div>
                </div>

                {/* Location */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
                    <input
                        type="text"
                        value={filters.location}
                        onChange={(e) => handleChange('location', e.target.value)}
                        placeholder="City or State"
                        className="input-field"
                    />
                </div>

                {/* Condition */}
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Condition</label>
                    <select
                        value={filters.condition}
                        onChange={(e) => handleChange('condition', e.target.value)}
                        className="input-field"
                    >
                        <option value="">All Conditions</option>
                        <option value="NEW">New</option>
                        <option value="USED">Used</option>
                        <option value="CERTIFIED">Certified Pre-Owned</option>
                    </select>
                </div>

                {/* Action Buttons */}
                <div className="space-y-2 pt-4 border-t">
                    <button onClick={applyFilters} className="btn-primary w-full">
                        Apply Filters
                    </button>
                    <button onClick={clearFilters} className="btn-outline w-full">
                        Clear All
                    </button>
                </div>
            </div>
        </div>
    );
}
