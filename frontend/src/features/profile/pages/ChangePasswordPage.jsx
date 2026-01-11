import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../services/userService';

export default function ChangePasswordPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.newPassword !== formData.confirmPassword) {
            setError('New password and confirmation do not match');
            return;
        }

        setLoading(true);
        try {
            await userService.changePassword(formData);
            alert('Password changed successfully!');
            navigate('/profile');
        } catch (err) {
            setError(err.response?.data?.statusMessage || 'Failed to change password');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-md mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">Change Password</h1>

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6">
                    <div className="space-y-4 mb-6">
                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Current Password</label>
                            <input
                                type="password"
                                value={formData.currentPassword}
                                onChange={(e) => setFormData(prev => ({ ...prev, currentPassword: e.target.value }))}
                                required
                                className="input-field"
                            />
                        </div>

                        <div>
                            <label className="block text-gray-700 font-medium mb-2">New Password</label>
                            <input
                                type="password"
                                value={formData.newPassword}
                                onChange={(e) => setFormData(prev => ({ ...prev, newPassword: e.target.value }))}
                                required
                                minLength={8}
                                className="input-field"
                            />
                            <p className="text-sm text-gray-500 mt-1">
                                Must be at least 8 characters with uppercase, lowercase, and number
                            </p>
                        </div>

                        <div>
                            <label className="block text-gray-700 font-medium mb-2">Confirm New Password</label>
                            <input
                                type="password"
                                value={formData.confirmPassword}
                                onChange={(e) => setFormData(prev => ({ ...prev, confirmPassword: e.target.value }))}
                                required
                                className="input-field"
                            />
                        </div>
                    </div>

                    <div className="flex gap-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="btn-primary flex-1"
                        >
                            {loading ? 'Changing...' : 'Change Password'}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate('/profile')}
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
