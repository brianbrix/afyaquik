import React, { useState } from 'react';
import { authService } from '../utils/authService';

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        const result = await authService.login(username, password);
        setLoading(false);

        if (result.token) {
            const params = new URLSearchParams(window.location.search);
            const redirect = params.get('redirect');

            if (redirect && redirect.startsWith('/client/')) {
                window.location.href = redirect;
            } else {
                window.location.href = '/client/admin/index.html';
            }
        } else {
            alert('Login failed');
        }
    };

    return (
        <div className="container d-flex align-items-center justify-content-center min-vh-100">
            <form onSubmit={handleSubmit} className="border p-4 rounded shadow-sm bg-white" style={{ maxWidth: '400px', width: '100%' }}>
                <h2 className="mb-4 text-primary text-center">AfyaQuik Login</h2>

                <div className="mb-3">
                    <input className="form-control" value={username} onChange={e => setUsername(e.target.value)} placeholder="Username" required />
                </div>

                <div className="mb-3">
                    <input type="password" className="form-control" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" required />
                </div>

                <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>
        </div>
    );
}
