import React, { useEffect, useState } from 'react';

interface AuthGuardProps {
    children: React.ReactNode;
    requiredRoles?: string[];
}

const isAuthenticated = async (): Promise<boolean> => {
    try {
        const response = await fetch("/api/auth/validate-token", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include'
        });

        if (response.status === 200) {
            localStorage.setItem('isLoggedIn', 'true');
            return true;
        } else {
            localStorage.removeItem('isLoggedIn');
            return false;
        }
    } catch (error) {
        console.error('Authentication check failed:', error);
        localStorage.removeItem('isLoggedIn');
        return false;
    }
};

const userHasAnyRole = (requiredRoles: string[]): boolean => {
    const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');
    return requiredRoles.some(role => roles.includes(role));
};

const AuthGuard = ({ children, requiredRoles = [] }: AuthGuardProps) => {
    const [checked, setChecked] = useState(false);

    useEffect(() => {
        const checkAuth = async () => {
            const currentPath = window.location.pathname + window.location.search + window.location.hash;

            try {
                const authenticated = await isAuthenticated();

                if (!authenticated) {
                    window.location.href = `/client/auth/index.html#/login?redirect=${encodeURIComponent(currentPath)}`;
                    return;
                }

                if (requiredRoles.length > 0 && !userHasAnyRole(requiredRoles)) {
                    window.location.href = `/client/auth/index.html#/home`;
                    return;
                }

                setChecked(true);
            } catch (error) {
                console.error('Authentication check failed:', error);
                window.location.href = `/client/auth/index.html#/login?redirect=${encodeURIComponent(currentPath)}`;
            }
        };

        checkAuth();
    }, [requiredRoles]);

    if (!checked) {
        return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;
    }

    return <>{children}</>;
};

export default AuthGuard;
