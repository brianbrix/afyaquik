import React, { useEffect, useState } from 'react';

const isAuthenticated = () => {
    return !!localStorage.getItem('authToken');
};

const AuthGuard = ({ children }: { children: React.ReactNode }) => {
    const [checked, setChecked] = useState(false);

    useEffect(() => {
        if (!isAuthenticated()) {
            const currentPath = window.location.pathname + window.location.search + window.location.hash;
            window.location.href = `/client/auth/index.html#/login?redirect=${encodeURIComponent(currentPath)}`;
        } else {
            setChecked(true);
        }
    }, []);

    if (!checked) {
        return <div className="text-center mt-5"><div className="spinner-border text-primary"></div></div>;
    }

    return <>{children}</>;
};

export default AuthGuard;
