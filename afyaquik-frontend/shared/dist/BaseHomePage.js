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
exports.BaseHomePage = void 0;
const react_1 = __importStar(require("react"));
const session_1 = require("./session");
const LOGIN_URL = (0, session_1.portalUrl)('auth', '/login');
const hasRole = (roles) => {
    if (localStorage.getItem('isLoggedIn') !== 'true')
        return false;
    return (0, session_1.sessionRoles)().some(role => roles.includes(role));
};
const BaseHomePage = ({ modules }) => {
    const [error, setError] = (0, react_1.useState)('');
    const [opening, setOpening] = (0, react_1.useState)('');
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    if (!isLoggedIn) {
        window.location.replace(LOGIN_URL);
        return null;
    }
    const handleNavigation = (mod) => __awaiter(void 0, void 0, void 0, function* () {
        if (hasRole(mod.requiredRoles)) {
            setOpening(mod.name);
            setError('');
            try {
                const current = localStorage.getItem('currentRole') || '';
                const role = mod.requiredRoles.includes(current) ? current : (0, session_1.sessionRoles)().find(value => mod.requiredRoles.includes(value));
                if (role)
                    yield (0, session_1.selectRole)(role);
                window.location.href = mod.path;
            }
            catch (_a) {
                setError('Unable to open this module. Please retry.');
                setOpening('');
            }
        }
        else {
            window.location.href = LOGIN_URL;
        }
    });
    const visibleModules = modules.filter(mod => hasRole(mod.requiredRoles));
    return (react_1.default.createElement(react_1.default.Fragment, null,
        react_1.default.createElement("div", { className: "container py-5" },
            react_1.default.createElement("h2", { className: "text-center mb-4 text-primary" }, "AfyaQuik System Modules"),
            error && react_1.default.createElement("div", { className: "alert alert-warning", role: "alert" }, error),
            visibleModules.length === 0 ? (react_1.default.createElement("p", { className: "text-center text-muted" }, "No modules are assigned to your current role. Contact an administrator.")) : (react_1.default.createElement("div", { className: "row row-cols-1 row-cols-md-2 g-4" }, visibleModules.map((mod) => (react_1.default.createElement("div", { className: "col", key: mod.name },
                react_1.default.createElement("div", { className: "card h-100 shadow-sm" },
                    react_1.default.createElement("div", { className: "card-body d-flex flex-column" },
                        react_1.default.createElement("h5", { className: "card-title d-flex align-items-center" },
                            react_1.default.createElement("i", { className: `${mod.icon} me-2 text-primary`, style: { fontSize: '1.5rem' } }),
                            mod.name),
                        react_1.default.createElement("p", { className: "card-text flex-grow-1" }, mod.description),
                        react_1.default.createElement("button", { className: "btn btn-primary mt-3 w-100", disabled: !!opening, onClick: () => handleNavigation(mod) },
                            react_1.default.createElement("i", { className: "bi-box-arrow-in-right me-1" }),
                            " Go to ",
                            mod.name)))))))))));
};
exports.BaseHomePage = BaseHomePage;
