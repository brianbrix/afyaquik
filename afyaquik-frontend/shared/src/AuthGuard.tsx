import React, { useEffect, useState } from 'react';

interface AuthGuardProps {
    children: React.ReactNode;
    requiredRoles?: string[];
}
const isAuthenticated = () => {
    const validToken =  fetch("/api/auth/validate-token",{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'credentials': 'include',
        }
    })
    validToken.then((res) => {
        if(res.status === 200){
            localStorage.setItem('isLoggedIn', 'true');
        }
        else
            {
            localStorage.clear();
        }
    })
    return !!localStorage.getItem('isLoggedIn');
};
const userHasAnyRole = (requiredRoles: string[]): boolean => {
    const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');
    return requiredRoles.some(role => roles.includes(role));
};

const AuthGuard = ({ children, requiredRoles = [] }: AuthGuardProps) => {
    const [checked, setChecked] = useState(false);

    useEffect(() => {
        const currentPath = window.location.pathname + window.location.search + window.location.hash;

        if (!isAuthenticated()) {
            window.location.href = `/client/auth/index.html#/login?redirect=${encodeURIComponent(currentPath)}`;
            return;
        }

        if (requiredRoles.length > 0 && !userHasAnyRole(requiredRoles)) {
            window.location.href = `/client/auth/index.html#/home`;
            return;
        }

        setChecked(true);
    }, [requiredRoles]);

    if (!checked) {
        return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;
    }

    return <>{children}</>;
};

export default AuthGuard;
