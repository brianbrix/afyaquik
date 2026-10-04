export declare const PORTAL_ROLES: {
    auth: never[];
    admin: string[];
    doctor: string[];
    nurse: string[];
    pharmacy: string[];
    receptionist: string[];
    reports: string[];
};
type Portal = keyof typeof PORTAL_ROLES;
export declare function portalUrl(portal: Portal, route?: string): string;
export declare function safePortalRedirect(candidate: string | null): string | null;
export declare function sessionRoles(): string[];
export declare function clearSession(): void;
export declare function saveSession(userId: number, roles: string[]): void;
export declare function selectRole(role: string): Promise<void>;
export {};
