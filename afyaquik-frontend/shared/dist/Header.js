"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
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
const react_1 = __importStar(require("react"));
const api_1 = __importDefault(require("./api"));
const index_1 = require("./index");
const session_1 = require("./session");
const HUB_URL = (0, session_1.portalUrl)('auth', '/home');
const LOGIN_URL = (0, session_1.portalUrl)('auth', '/login');
const Header = ({ homeUrl, userRole }) => {
    const [isLoggedIn, setLoggedIn] = (0, react_1.useState)(localStorage.getItem('isLoggedIn') === 'true');
    const [loggingOut, setLoggingOut] = (0, react_1.useState)(false);
    const [error, setError] = (0, react_1.useState)('');
    const [currentRole, setCurrentRole] = (0, react_1.useState)(localStorage.getItem('currentRole') || 'USER');
    const [userId, setUserId] = (0, react_1.useState)(Number(localStorage.getItem('userId')));
    (0, react_1.useEffect)(() => {
        const refresh = () => {
            setLoggedIn(localStorage.getItem('isLoggedIn') === 'true');
            setCurrentRole(localStorage.getItem('currentRole') || 'USER');
            setUserId(Number(localStorage.getItem('userId')));
        };
        window.addEventListener('afyaquik-session', refresh);
        window.addEventListener('storage', refresh);
        return () => {
            window.removeEventListener('afyaquik-session', refresh);
            window.removeEventListener('storage', refresh);
        };
    }, []);
    const handleLogout = () => __awaiter(void 0, void 0, void 0, function* () {
        setLoggingOut(true);
        setError('');
        try {
            yield (0, api_1.default)('/auth/logout', { method: 'POST', body: {} });
            (0, session_1.clearSession)();
            window.location.replace(LOGIN_URL);
        }
        catch (_a) {
            setError('Sign out failed. Please retry.');
            setLoggingOut(false);
        }
    });
    return (react_1.default.createElement("nav", { className: "navbar navbar-expand-lg navbar-dark bg-primary px-3" },
        react_1.default.createElement("a", { className: "navbar-brand mb-0 h1", href: homeUrl !== null && homeUrl !== void 0 ? homeUrl : HUB_URL }, "AfyaQuik Health"),
        isLoggedIn && homeUrl && (react_1.default.createElement("a", { href: HUB_URL, className: "nav-link text-light ms-2" },
            react_1.default.createElement("i", { className: "bi bi-grid-fill me-1" }),
            " All Modules")),
        isLoggedIn ? (react_1.default.createElement(react_1.default.Fragment, null,
            react_1.default.createElement(index_1.NotificationsBell, { userId: userId, userRole: userRole !== null && userRole !== void 0 ? userRole : currentRole }),
            react_1.default.createElement("a", { href: (0, session_1.portalUrl)('auth', '/profile'), className: "nav-link text-light ms-3" },
                react_1.default.createElement("i", { className: "bi bi-person-circle me-1" }),
                " Profile"),
            error && react_1.default.createElement("span", { role: "alert", className: "text-light mx-2" }, error),
            react_1.default.createElement("button", { className: "btn btn-light text-primary ms-auto", disabled: loggingOut, onClick: handleLogout },
                react_1.default.createElement("i", { className: "bi-box-arrow-right me-1" }),
                " Logout"))) : (react_1.default.createElement("button", { className: "btn btn-light text-primary ms-auto", onClick: () => { window.location.href = LOGIN_URL; } },
            react_1.default.createElement("i", { className: "bi-box-arrow-in-right me-1" }),
            " Login"))));
};
exports.default = Header;
