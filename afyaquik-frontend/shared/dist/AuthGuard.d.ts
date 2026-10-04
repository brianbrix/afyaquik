import React from 'react';
interface AuthGuardProps {
    children: React.ReactNode;
    requiredRoles?: string[];
}
declare const AuthGuard: ({ children, requiredRoles }: AuthGuardProps) => React.JSX.Element;
export default AuthGuard;
