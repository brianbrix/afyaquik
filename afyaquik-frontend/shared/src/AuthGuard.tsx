import React, { useEffect, useState } from 'react';
import apiRequest, { ApiError } from './api';
import { clearSession, portalUrl, saveSession, selectRole } from './session';

interface AuthGuardProps {
    children: React.ReactNode;
    requiredRoles?: string[];
}

const AuthGuard = ({ children, requiredRoles }: AuthGuardProps) => {
    const [checked, setChecked] = useState(false);
    const [error, setError] = useState('');
    const [attempt, setAttempt] = useState(0);
    const roleKey = (requiredRoles || []).join(',');

    useEffect(() => {
        let active = true;
        setChecked(false);
        setError('');
        const check = async () => {
            try {
                const user = await apiRequest('/users/me');
                if (!active) return;
                if (!Number.isInteger(user.id) || user.id <= 0 || !Array.isArray(user.roles)) {
                    throw new Error('The session response was invalid.');
                }
                const roles: string[] = user.roles.filter((role: unknown): role is string => typeof role === 'string');
                saveSession(user.id, roles);
                const required = roleKey ? roleKey.split(',') : [];
                if (required.length && !required.some(role => roles.includes(role))) {
                    window.location.replace(portalUrl('auth', '/home'));
                    return;
                }
                const current = localStorage.getItem('currentRole');
                const role = required.length && (!current || !required.includes(current))
                    ? roles.find(value => required.includes(value)) : current;
                if (role && (role !== current || !localStorage.getItem('formattedStations'))) {
                    await selectRole(role);
                }
                if (active) setChecked(true);
            } catch (failure) {
                if (!active) return;
                if (failure instanceof ApiError && failure.status === 401) {
                    clearSession();
                    window.location.replace(portalUrl('auth', `/login?redirect=${encodeURIComponent(window.location.href)}`));
                } else {
                    setError('Unable to verify your session. Check your connection and try again.');
                }
            }
        };
        check();
        return () => { active = false; };
    }, [roleKey, attempt]);

    if (error) return <div className="container py-4"><div className="alert alert-warning" role="alert">{error}</div>
        <button className="btn btn-primary" onClick={() => setAttempt(value => value + 1)}><i className="bi bi-arrow-clockwise me-2" />Retry</button></div>;
    if (!checked) {
        return <div className="text-center mt-5" role="status"><div className="spinner-border text-primary" /><span className="visually-hidden">Checking session</span></div>;
    }

    return <>{children}</>;
};

export default AuthGuard;
