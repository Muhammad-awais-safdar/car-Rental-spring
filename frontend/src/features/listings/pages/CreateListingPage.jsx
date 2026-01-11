import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { listingService } from '../services/listingService';

export default function CreateListingPage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
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
        images: [],
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        // In production, upload images to server and get URLs
        // For now, using placeholder URLs
        const imageUrls = files.map((file, index) =>
            `https://via.placeholder.com/800x600?text=Image+${index + 1}`
        );
        setFormData(prev => ({ ...prev, images: imageUrls }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await listingService.createListing({
                ...formData,
                price: parseFloat(formData.price),
                year: parseInt(formData.year),
                mileage: parseInt(formData.mileage),
            });

            navigate('/my-listings');
        } catch (err) {
            setError(err.response?.data?.statusMessage || 'Failed to create listing');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">Create New Listing</h1>

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
                            placeholder="e.g., 2023 Toyota Camry - Excellent Condition"
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
                            placeholder="Describe your car in detail..."
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
                            placeholder="25000"
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
                                placeholder="Toyota"
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
                                placeholder="Camry"
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
                                placeholder="2023"
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
                                placeholder="15000"
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
                                placeholder="New York, NY"
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

                    {/* Images */}
                    <div className="mb-6">
                        <label className="block text-gray-700 font-semibold mb-2">Images</label>
                        <input
                            type="file"
                            multiple
                            accept="image/*"
                            onChange={handleImageChange}
                            className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-[#EF4444] file:text-white hover:file:bg-[#dc2626]"
                        />
                        <p className="text-sm text-gray-500 mt-1">Upload up to 10 images</p>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="btn-primary flex-1"
                        >
                            {loading ? 'Creating...' : 'Create Listing'}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate(-1)}
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
