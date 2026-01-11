import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { listingService } from '../services/listingService';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';

export default function EditListingPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    const [formData, setFormData] = useState({
        title: '',
        description: '',
        price: '',
        make: '',
        model: '',
        year: '',
        mileage: '',
        location: '',
        condition: 'USED',
        listingType: 'SELL',
    });

    useEffect(() => {
        fetchListing();
    }, [id]);

    const fetchListing = async () => {
        try {
            const response = await listingService.getListingById(id);
            const listing = response.data;
            setFormData({
                title: listing.title,
                description: listing.description,
                price: listing.price.toString(),
                make: listing.make,
                model: listing.model,
                year: listing.year.toString(),
                mileage: listing.mileage.toString(),
                location: listing.location,
                condition: listing.condition,
                listingType: listing.listingType,
            });
        } catch (error) {
            setError('Failed to load listing');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSubmitting(true);

        try {
            await listingService.updateListing(id, {
                ...formData,
                price: parseFloat(formData.price),
                year: parseInt(formData.year),
                mileage: parseInt(formData.mileage),
            });

            navigate('/my-listings');
        } catch (err) {
            setError(err.response?.data?.statusMessage || 'Failed to update listing');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">Edit Listing</h1>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6">
                    {/* Listing Type */}
                    <div className="mb-6">
                        <label className="block text-gray-700 font-semibold mb-2">Listing Type *</label>
                        <select
                            name="listingType"
                            value={formData.listingType}
                            onChange={handleChange}
                            required
                            className="input-field"
                        >
                            <option value="SELL">For Sale</option>
                            <option value="RENT">For Rent</option>
                        </select>
                    </div>

                    {/* Title */}
                    <div className="mb-6">
                        <label className="block text-gray-700 font-semibold mb-2">Title *</label>
                        <input
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            required
                            className="input-field"
                        />
                    </div>

                    {/* Description */}
                    <div className="mb-6">
                        <label className="block text-gray-700 font-semibold mb-2">Description *</label>
                        <textarea
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            required
                            rows={5}
                            className="input-field"
                        />
                    </div>

                    {/* Price */}
                    <div className="mb-6">
                        <label className="block text-gray-700 font-semibold mb-2">Price ($) *</label>
                        <input
                            type="number"
                            name="price"
                            value={formData.price}
                            onChange={handleChange}
                            required
                            min="0"
                            step="0.01"
                            className="input-field"
                        />
                    </div>

                    {/* Make and Model */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Make *</label>
                            <input
                                type="text"
                                name="make"
                                value={formData.make}
                                onChange={handleChange}
                                required
                                className="input-field"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Model *</label>
                            <input
                                type="text"
                                name="model"
                                value={formData.model}
                                onChange={handleChange}
                                required
                                className="input-field"
                            />
                        </div>
                    </div>

                    {/* Year and Mileage */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Year *</label>
                            <input
                                type="number"
                                name="year"
                                value={formData.year}
                                onChange={handleChange}
                                required
                                min="1900"
                                max={new Date().getFullYear() + 1}
                                className="input-field"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Mileage (km) *</label>
                            <input
                                type="number"
                                name="mileage"
                                value={formData.mileage}
                                onChange={handleChange}
                                required
                                min="0"
                                className="input-field"
                            />
                        </div>
                    </div>

                    {/* Location and Condition */}
                    <div className="grid grid-cols-2 gap-4 mb-6">
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Location *</label>
                            <input
                                type="text"
                                name="location"
                                value={formData.location}
                                onChange={handleChange}
                                required
                                className="input-field"
                            />
                        </div>
                        <div>
                            <label className="block text-gray-700 font-semibold mb-2">Condition *</label>
                            <select
                                name="condition"
                                value={formData.condition}
                                onChange={handleChange}
                                required
                                className="input-field"
                            >
                                <option value="NEW">New</option>
                                <option value="USED">Used</option>
                                <option value="CERTIFIED">Certified Pre-Owned</option>
                            </select>
                        </div>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-4">
                        <button
                            type="submit"
                            disabled={submitting}
                            className="btn-primary flex-1"
                        >
                            {submitting ? 'Updating...' : 'Update Listing'}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate('/my-listings')}
                            className="btn-secondary"
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
