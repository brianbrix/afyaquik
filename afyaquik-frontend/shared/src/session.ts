import apiRequest from './api';

export const PORTAL_ROLES = {
    auth: [],
    admin: ['ADMIN', 'SUPERADMIN'],
    doctor: ['DOCTOR'],
    nurse: ['NURSE'],
    pharmacy: ['PHARMACIST'],
    receptionist: ['RECEPTIONIST'],
    reports: ['REPORTS', 'ADMIN', 'SUPERADMIN', 'DOCTOR', 'NURSE'],
};

type Portal = keyof typeof PORTAL_ROLES;
const PORTS: Record<Portal, number> = { auth: 3000, admin: 3001, doctor: 3003, receptionist: 3004, reports: 3005, pharmacy: 3006, nurse: 3007 };

export function portalUrl(portal: Portal, route = ''): string {
    const pathname = `/client/${portal}/index.html${route ? `#${route}` : ''}`;
    if (process.env.NODE_ENV !== 'development') return pathname;
    const origin = new URL(window.location.origin);
    origin.port = String(PORTS[portal]);
    return `${origin.origin}${pathname}`;
}

export function safePortalRedirect(candidate: string | null): string | null {
    if (!candidate) return null;
    try {
        const url = new URL(candidate, window.location.origin);
        if (!['http:', 'https:'].includes(url.protocol) || url.username || url.password) return null;
        const portals = Object.keys(PORTS) as Portal[];
        const knownPortal = portals.find(portal => url.pathname === `/client/${portal}/index.html`);
        if (url.origin === window.location.origin && knownPortal) return url.href;
        if (process.env.NODE_ENV === 'development' && url.hostname === window.location.hostname
            && url.protocol === window.location.protocol) {
            const portal = portals.find(value => String(PORTS[value]) === url.port);
            if (portal && (url.pathname === '/' || url.pathname === '/index.html' || portal === knownPortal)) return url.href;
        }
        return null;
    } catch {
        return null;
    }
}

export function sessionRoles(): string[] {
    try {
        const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');
        return Array.isArray(roles) ? roles.filter((role): role is string => typeof role === 'string') : [];
    } catch {
        return [];
    }
}

export function clearSession(): void {
    ['isLoggedIn', 'userId', 'userRoles', 'currentRole', 'allowedStations', 'formattedStations'].forEach(key => localStorage.removeItem(key));
    window.dispatchEvent(new Event('afyaquik-session'));
}

export function saveSession(userId: number, roles: string[]): void {
    localStorage.setItem('isLoggedIn', 'true');
    localStorage.setItem('userId', String(userId));
    localStorage.setItem('userRoles', JSON.stringify(roles));
    if (!roles.includes(localStorage.getItem('currentRole') || '')) {
        localStorage.removeItem('allowedStations');
        localStorage.removeItem('formattedStations');
        if (roles.length) localStorage.setItem('currentRole', roles[0]);
        else localStorage.removeItem('currentRole');
    }
    window.dispatchEvent(new Event('afyaquik-session'));
}

export async function selectRole(role: string): Promise<void> {
    if (!sessionRoles().includes(role)) throw new Error('This role is not assigned to your account.');
    const result = await apiRequest(`/roles/byName/${encodeURIComponent(role)}`);
    const stations: string[] = Array.isArray(result.stations) ? result.stations : [];
    localStorage.setItem('currentRole', role);
    localStorage.setItem('allowedStations', JSON.stringify(stations));
    localStorage.setItem('formattedStations', stations.join('||'));
    window.dispatchEvent(new Event('afyaquik-session'));
}