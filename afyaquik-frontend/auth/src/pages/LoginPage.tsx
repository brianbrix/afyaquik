import React, { useState } from 'react';
import { authService } from '../utils/authService';
import {sendNotification} from "@afyaquik/shared";

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [roles, setRoles] = useState<string[]>([]);
    const [selectedRole, setSelectedRole] = useState('');

    const getRedirectParam = () => {
        const hash = window.location.hash;
        const queryString = hash.split('?')[1];
        if (!queryString) return null;

        const params = new URLSearchParams(queryString);
        return params.get('redirect');
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const result = await authService.login(username, password);
            setLoading(false);

            if (authService.isLoggedIn() && result.roles?.length > 0) {
                setRoles(result.roles);
            } else {
                if (authService.isLoggedIn() && result.roles?.length<=0)
                {
                    sendNotification(
                        null,'User issue alert',
                        `The user ${result.userId} does not have any roles set`,
                        `/client/admin/index.html`,
                        'SYSTEM', `SUPERADMIN`
                    )
                }
                setError('Invalid username or password.');
            }
        } catch (err) {
            setLoading(false);
            setError('An unexpected error occurred. Please try again.');
        }
    };

    const handleRoleConfirm = () => {
        if (!selectedRole) {
            setError('Please select a role to continue.');
            return;
        }

        localStorage.setItem('currentRole', selectedRole);

        const redirect = getRedirectParam();
        if (redirect && redirect.startsWith('/client/')) {
            window.location.href = redirect;
        } else {
            window.location.href = '/client/auth/index.html#/home';
        }
    };

    return (
        <div className="container d-flex align-items-center justify-content-center min-vh-100">
            <form onSubmit={handleSubmit} className="border p-4 rounded shadow-sm bg-white" style={{ maxWidth: '400px', width: '100%' }}>
                <h2 className="mb-4 text-primary text-center">AfyaQuik Login</h2>

                {error && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        {error}
                        <button type="button" className="btn-close" aria-label="Close" onClick={() => setError(null)}></button>
                    </div>
                )}

                {!roles.length ? (
                    <>
                        <div className="mb-3">
                            <input
                                className="form-control"
                                value={username}
                                onChange={e => setUsername(e.target.value)}
                                placeholder="Username"
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <input
                                type="password"
                                className="form-control"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                placeholder="Password"
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                            {loading ? 'Logging in...' : 'Login'}
                        </button>
                    </>
                ) : (
                    <>
                        <div className="mb-3">
                            <label className="form-label fw-semibold">Select Role</label>
                            <select
                                className="form-select"
                                value={selectedRole}
                                onChange={e => setSelectedRole(e.target.value)}
                            >
                                <option value="">Choose a role</option>
                                {roles.map(role => (
                                    <option key={role} value={role}>
                                        {role}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <button type="button" className="btn btn-success w-100" onClick={handleRoleConfirm}>
                            Continue
                        </button>
                    </>
                )}
            </form>
        </div>
    );
}
