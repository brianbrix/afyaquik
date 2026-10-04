import React, { ReactNode } from 'react';
export type ToastType = 'success' | 'error' | 'info' | 'warning';
interface ToastContextProps {
    showToast: (message: string, type?: ToastType) => void;
}
export declare const ToastProvider: ({ children }: {
    children: ReactNode;
}) => React.JSX.Element;
export declare const useToast: () => ToastContextProps;
export {};
