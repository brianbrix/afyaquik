"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.PORTAL_ROLES = void 0;
exports.portalUrl = portalUrl;
exports.safePortalRedirect = safePortalRedirect;
exports.sessionRoles = sessionRoles;
exports.clearSession = clearSession;
exports.saveSession = saveSession;
exports.selectRole = selectRole;
const api_1 = __importDefault(require("./api"));
exports.PORTAL_ROLES = {
    auth: [],
    admin: ['ADMIN', 'SUPERADMIN'],
    doctor: ['DOCTOR'],
    nurse: ['NURSE'],
    pharmacy: ['PHARMACIST'],
    receptionist: ['RECEPTIONIST'],
    reports: ['REPORTS', 'ADMIN', 'SUPERADMIN', 'DOCTOR', 'NURSE'],
};
const PORTS = { auth: 3000, admin: 3001, doctor: 3003, receptionist: 3004, reports: 3005, pharmacy: 3006, nurse: 3007 };
function portalUrl(portal, route = '') {
    const pathname = `/client/${portal}/index.html${route ? `#${route}` : ''}`;
    if (process.env.NODE_ENV !== 'development')
        return pathname;
    const origin = new URL(window.location.origin);
    origin.port = String(PORTS[portal]);
    return `${origin.origin}${pathname}`;
}
function safePortalRedirect(candidate) {
    if (!candidate)
        return null;
    try {
        const url = new URL(candidate, window.location.origin);
        if (!['http:', 'https:'].includes(url.protocol) || url.username || url.password)
            return null;
        const portals = Object.keys(PORTS);
        const knownPortal = portals.find(portal => url.pathname === `/client/${portal}/index.html`);
        if (url.origin === window.location.origin && knownPortal)
            return url.href;
        if (process.env.NODE_ENV === 'development' && url.hostname === window.location.hostname
            && url.protocol === window.location.protocol) {
            const portal = portals.find(value => String(PORTS[value]) === url.port);
            if (portal && (url.pathname === '/' || url.pathname === '/index.html' || portal === knownPortal))
                return url.href;
        }
        return null;
    }
    catch (_a) {
        return null;
    }
}
function sessionRoles() {
    try {
        const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');
        return Array.isArray(roles) ? roles.filter((role) => typeof role === 'string') : [];
    }
    catch (_a) {
        return [];
    }
}
function clearSession() {
    ['isLoggedIn', 'userId', 'userRoles', 'currentRole', 'allowedStations', 'formattedStations'].forEach(key => localStorage.removeItem(key));
    window.dispatchEvent(new Event('afyaquik-session'));
}
function saveSession(userId, roles) {
    localStorage.setItem('isLoggedIn', 'true');
    localStorage.setItem('userId', String(userId));
    localStorage.setItem('userRoles', JSON.stringify(roles));
    if (!roles.includes(localStorage.getItem('currentRole') || '')) {
        localStorage.removeItem('allowedStations');
        localStorage.removeItem('formattedStations');
        if (roles.length)
            localStorage.setItem('currentRole', roles[0]);
        else
            localStorage.removeItem('currentRole');
    }
    window.dispatchEvent(new Event('afyaquik-session'));
}
function selectRole(role) {
    return __awaiter(this, void 0, void 0, function* () {
        if (!sessionRoles().includes(role))
            throw new Error('This role is not assigned to your account.');
        const result = yield (0, api_1.default)(`/roles/byName/${encodeURIComponent(role)}`);
        const stations = Array.isArray(result.stations) ? result.stations : [];
        localStorage.setItem('currentRole', role);
        localStorage.setItem('allowedStations', JSON.stringify(stations));
        localStorage.setItem('formattedStations', stations.join('||'));
        window.dispatchEvent(new Event('afyaquik-session'));
    });
}
