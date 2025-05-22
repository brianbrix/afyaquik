import React, { createContext, useContext, useState, ReactNode } from 'react';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

interface Toast {
    message: string;
    type: ToastType;
}

interface ToastContextProps {
    showToast: (message: string, type?: ToastType) => void;
}

const ToastContext = createContext<ToastContextProps | undefined>(undefined);

export const ToastProvider = ({ children }: { children: ReactNode }) => {
    const [toast, setToast] = useState<Toast | null>(null);

    const showToast = (message: string, type: ToastType = 'info') => {
        setToast({ message, type });
        setTimeout(() => setToast(null), 6000); // auto-dismiss after 6s
    };

    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            {toast && (
                <div

                    style={{
                        position: 'fixed',
                        top: '50%',
                        left: '50%',
                        transform: 'translate(-50%, -50%)',
                        padding: '16px 24px',
                        borderRadius: '8px',
                        color: toast.type === 'warning' ? '#212529' : 'white',
                        fontWeight: 500,
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
                        zIndex: 1000,
                        animation: 'fadeIn 0.3s ease-in-out'
                    }}
                    className={`toast-container toast-${toast.type}`}>
                    {toast.message}
                </div>
            )}
        </ToastContext.Provider>
    );
};

export const useToast = () => {
    const context = useContext(ToastContext);
    if (!context) throw new Error('useToast must be used within a ToastProvider');
    return context;
};
