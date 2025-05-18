import React, { createContext, useState, useContext } from 'react';
import { authService } from '../utils/authService';

const AuthContext = createContext<{ isLoggedIn: boolean; logout: () => void }>({ isLoggedIn: false, logout: () => {} });

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(authService.isLoggedIn());

    const logout = () => {
        authService.logout();
        setIsLoggedIn(false);
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
