import React from 'react';
export interface AfyaQuikModule {
    name: string;
    path: string;
    description: string;
    icon: string;
    requiredRoles: string[];
    currentRole?: string;
}
interface BaseHomePageProps {
    modules: AfyaQuikModule[];
}
export declare const BaseHomePage: React.FC<BaseHomePageProps>;
export {};
