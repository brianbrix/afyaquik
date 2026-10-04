import React, { ReactNode } from 'react';
export type AlertType = 'success' | 'error' | 'warning' | 'info';
interface AlertContextProps {
    showAlert: (message: string, title?: string, type?: AlertType) => void;
}
export declare const AlertProvider: ({ children }: {
    children: ReactNode;
}) => React.JSX.Element;
export declare const useAlert: () => AlertContextProps;
export {};
