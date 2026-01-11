import { useState, useEffect } from 'react';
import { userService } from '../services/userService';
import LoadingSpinner from '../../../shared/components/LoadingSpinner';

export default function ProfilePage() {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [editing, setEditing] = useState(false);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        phone: '',
    });

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            const response = await userService.getProfile();
            setProfile(response.data);
            setFormData({
                firstName: response.data.firstName,
                lastName: response.data.lastName,
                phone: response.data.phone || '',
            });
        } catch (error) {
            console.error('Error fetching profile:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await userService.updateProfile(formData);
            await fetchProfile();
            setEditing(false);
            alert('Profile updated successfully!');
        } catch (error) {
            alert('Failed to update profile');
        }
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">My Profile</h1>

                <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                    {!editing ? (
                        <>
                            <div className="flex justify-between items-start mb-6">
                                <div>
                                    <h2 className="text-2xl font-semibold text-gray-900">
                                        {profile.firstName} {profile.lastName}
                                    </h2>
                                    <p className="text-gray-600">{profile.email}</p>
                                </div>
                                <button onClick={() => setEditing(true)} className="btn-primary">
                                    Edit Profile
                                </button>
                            </div>

                            <div className="grid grid-cols-2 gap-6">
                                <div>
                                    <p className="text-gray-600 text-sm">Phone</p>
                                    <p className="font-semibold text-gray-900">{profile.phone || 'Not provided'}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">Account Status</p>
                                    <p className="font-semibold text-gray-900">
                                        {profile.isActive ? '✅ Active' : '❌ Inactive'}
                                    </p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">Verified</p>
                                    <p className="font-semibold text-gray-900">
                                        {profile.isVerified ? '✅ Verified' : '⏳ Pending'}
                                    </p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">Roles</p>
                                    <p className="font-semibold text-gray-900">{profile.roles?.join(', ')}</p>
                                </div>
                            </div>
                        </>
                    ) : (
                        <form onSubmit={handleSubmit}>
                            <div className="space-y-4 mb-6">
                                <div>
                                    <label className="block text-gray-700 font-medium mb-2">First Name</label>
                                    <input
                                        type="text"
                                        value={formData.firstName}
                                        onChange={(e) => setFormData(prev => ({ ...prev, firstName: e.target.value }))}
                                        className="input-field"
                                    />
                                </div>

                                <div>
                                    <label className="block text-gray-700 font-medium mb-2">Last Name</label>
                                    <input
                                        type="text"
                                        value={formData.lastName}
                                        onChange={(e) => setFormData(prev => ({ ...prev, lastName: e.target.value }))}
                                        className="input-field"
                                    />
                                </div>

                                <div>
                                    <label className="block text-gray-700 font-medium mb-2">Phone</label>
                                    <input
                                        type="tel"
                                        value={formData.phone}
                                        onChange={(e) => setFormData(prev => ({ ...prev, phone: e.target.value }))}
                                        className="input-field"
                                    />
                                </div>
                            </div>

                            <div className="flex gap-4">
                                <button type="submit" className="btn-primary">
                                    Save Changes
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setEditing(false)}
                                    className="btn-secondary"
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    )}
                </div>

                <div className="bg-white rounded-lg shadow-md p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Account Security</h3>
                    <button
                        onClick={() => window.location.href = '/profile/change-password'}
                        className="btn-outline"
                    >
                        Change Password
                    </button>
                </div>
            </div>
        </div>
    );
}
