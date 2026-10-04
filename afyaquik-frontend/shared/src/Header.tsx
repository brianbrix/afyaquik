import React, { useEffect, useState } from 'react';
import apiRequest from "./api";
import {NotificationsBell} from "./index";
import { clearSession, portalUrl } from './session';

const HUB_URL = portalUrl('auth', '/home');
const LOGIN_URL = portalUrl('auth', '/login');

interface HeaderProps {
    /** Link for the brand logo — use the module's own home page.
     *  Omit (or leave undefined) when rendering inside the auth/hub module itself. */
    homeUrl?: string;
    userRole?: string;
}

const Header: React.FC<HeaderProps> = ({homeUrl, userRole}) => {
    const [isLoggedIn, setLoggedIn] = useState(localStorage.getItem('isLoggedIn') === 'true');
    const [loggingOut, setLoggingOut] = useState(false);
    const [error, setError] = useState('');
    const [currentRole, setCurrentRole] = useState(localStorage.getItem('currentRole') || 'USER');
    const [userId, setUserId] = useState(Number(localStorage.getItem('userId')));

    useEffect(() => {
        const refresh = () => {
            setLoggedIn(localStorage.getItem('isLoggedIn') === 'true');
            setCurrentRole(localStorage.getItem('currentRole') || 'USER');
            setUserId(Number(localStorage.getItem('userId')));
        };
        window.addEventListener('afyaquik-session', refresh);
        window.addEventListener('storage', refresh);
        return () => {
            window.removeEventListener('afyaquik-session', refresh);
            window.removeEventListener('storage', refresh);
        };
    }, []);

    const handleLogout = async () => {
        setLoggingOut(true);
        setError('');
        try {
            await apiRequest('/auth/logout', { method: 'POST', body: {} });
            clearSession();
            window.location.replace(LOGIN_URL);
        } catch {
            setError('Sign out failed. Please retry.');
            setLoggingOut(false);
        }
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-3">
            {/* Brand — links to the module's own home, or the hub when inside auth */}
            <a className="navbar-brand mb-0 h1" href={homeUrl ?? HUB_URL}>
                AfyaQuik Health
            </a>

            {/* "All Modules" shortcut — only shown inside a sub-module (homeUrl is set) */}
            {isLoggedIn && homeUrl && (
                <a href={HUB_URL} className="nav-link text-light ms-2">
                    <i className="bi bi-grid-fill me-1"></i> All Modules
                </a>
            )}

            {isLoggedIn ? (
                <>
                    <NotificationsBell
                        userId={userId}
                        userRole={userRole ?? currentRole}
                    />
                    <a href={portalUrl('auth', '/profile')} className="nav-link text-light ms-3">
                        <i className="bi bi-person-circle me-1"></i> Profile
                    </a>
                    {error && <span role="alert" className="text-light mx-2">{error}</span>}
                    <button className="btn btn-light text-primary ms-auto" disabled={loggingOut} onClick={handleLogout}>
                        <i className="bi-box-arrow-right me-1"></i> Logout
                    </button>
                </>
            ) : (
                <button
                    className="btn btn-light text-primary ms-auto"
                    onClick={() => { window.location.href = LOGIN_URL; }}
                >
                    <i className="bi-box-arrow-in-right me-1"></i> Login
                </button>
            )}
        </nav>
    );
};

export default Header;
