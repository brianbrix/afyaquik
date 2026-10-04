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
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importStar(require("react"));
const api_1 = __importStar(require("./api"));
const session_1 = require("./session");
const AuthGuard = ({ children, requiredRoles }) => {
    const [checked, setChecked] = (0, react_1.useState)(false);
    const [error, setError] = (0, react_1.useState)('');
    const [attempt, setAttempt] = (0, react_1.useState)(0);
    const roleKey = (requiredRoles || []).join(',');
    (0, react_1.useEffect)(() => {
        let active = true;
        setChecked(false);
        setError('');
        const check = () => __awaiter(void 0, void 0, void 0, function* () {
            try {
                const user = yield (0, api_1.default)('/users/me');
                if (!active)
                    return;
                if (!Number.isInteger(user.id) || user.id <= 0 || !Array.isArray(user.roles)) {
                    throw new Error('The session response was invalid.');
                }
                const roles = user.roles.filter((role) => typeof role === 'string');
                (0, session_1.saveSession)(user.id, roles);
                const required = roleKey ? roleKey.split(',') : [];
                if (required.length && !required.some(role => roles.includes(role))) {
                    window.location.replace((0, session_1.portalUrl)('auth', '/home'));
                    return;
                }
                const current = localStorage.getItem('currentRole');
                const role = required.length && (!current || !required.includes(current))
                    ? roles.find(value => required.includes(value)) : current;
                if (role && (role !== current || !localStorage.getItem('formattedStations'))) {
                    yield (0, session_1.selectRole)(role);
                }
                if (active)
                    setChecked(true);
            }
            catch (failure) {
                if (!active)
                    return;
                if (failure instanceof api_1.ApiError && failure.status === 401) {
                    (0, session_1.clearSession)();
                    window.location.replace((0, session_1.portalUrl)('auth', `/login?redirect=${encodeURIComponent(window.location.href)}`));
                }
                else {
                    setError('Unable to verify your session. Check your connection and try again.');
                }
            }
        });
        check();
        return () => { active = false; };
    }, [roleKey, attempt]);
    if (error)
        return react_1.default.createElement("div", { className: "container py-4" },
            react_1.default.createElement("div", { className: "alert alert-warning", role: "alert" }, error),
            react_1.default.createElement("button", { className: "btn btn-primary", onClick: () => setAttempt(value => value + 1) },
                react_1.default.createElement("i", { className: "bi bi-arrow-clockwise me-2" }),
                "Retry"));
    if (!checked) {
        return react_1.default.createElement("div", { className: "text-center mt-5", role: "status" },
            react_1.default.createElement("div", { className: "spinner-border text-primary" }),
            react_1.default.createElement("span", { className: "visually-hidden" }, "Checking session"));
    }
    return react_1.default.createElement(react_1.default.Fragment, null, children);
};
exports.default = AuthGuard;
