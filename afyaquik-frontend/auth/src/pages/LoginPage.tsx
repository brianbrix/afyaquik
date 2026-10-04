import React, { useState } from 'react';
import { authService } from '../utils/authService';
import {ApiError, portalUrl, safePortalRedirect, selectRole} from "@afyaquik/shared";

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [roles, setRoles] = useState<string[]>([]);
    const [selectedRole, setSelectedRole] = useState('');

    const getRedirectParam = (): string | null => {
        const hash = window.location.hash;
        const queryString = hash.split('?')[1];
        if (!queryString) return null;
        const params = new URLSearchParams(queryString);
        return params.get('redirect');
    };

    const finishLogin = async (role: string) => {
        await selectRole(role);
        window.location.replace(safePortalRedirect(getRedirectParam()) || portalUrl('auth', '/home'));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const result = await authService.login(username, password);
            if (result.isLoggedIn && result.roles.length > 0) {
                if (result.roles.length === 1) await finishLogin(result.roles[0]);
                else setRoles(result.roles);
            } else if (result.isLoggedIn && result.roles.length === 0) {
                setError('Login successful but no roles are assigned to your account. Contact an administrator.');
            } else {
                setError('Invalid username or password.');
            }
        } catch (err) {
            setError(err instanceof ApiError && err.status === 401
                ? 'Invalid username or password.' : 'Unable to sign in. Please check your connection and try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleRoleConfirm = async () => {
        if (!selectedRole || !roles.includes(selectedRole)) {
            setError('Please select a role to continue.');
            return;
        }
        setLoading(true);
        setError(null);
        try {
            await finishLogin(selectedRole);
        } catch {
            setError('Unable to load your role. Please retry.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container py-5">
            <form onSubmit={handleSubmit} className="border p-4 rounded shadow-sm bg-white mx-auto" style={{ maxWidth: '400px', width: '100%' }}>
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
                            <label htmlFor="username" className="form-label">Username</label>
                            <input
                                id="username"
                                autoComplete="username"
                                className="form-control"
                                value={username}
                                onChange={e => setUsername(e.target.value)}
                                placeholder="Username"
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">Password</label>
                            <input
                                id="password"
                                autoComplete="current-password"
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

                        <button type="button" className="btn btn-success w-100" disabled={loading} onClick={handleRoleConfirm}>
                            Continue
                        </button>
                    </>
                )}
            </form>
            <div className="mt-3 text-center">
                <a
                    href={portalUrl('auth', '/forgot-password')}
                    className="text-primary"
                    style={{ cursor: 'pointer', textDecoration: 'underline' }}
                >
                    Forgot password?
                </a>
            </div>
        </div>
    );
}
