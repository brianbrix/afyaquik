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
Object.defineProperty(exports, "__esModule", { value: true });
exports.useAlert = exports.AlertProvider = void 0;
const react_1 = __importStar(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const AlertContext = (0, react_1.createContext)(undefined);
const AlertProvider = ({ children }) => {
    const [isOpen, setIsOpen] = (0, react_1.useState)(false);
    const [message, setMessage] = (0, react_1.useState)('');
    const [title, setTitle] = (0, react_1.useState)('Alert');
    const [alertType, setAlertType] = (0, react_1.useState)('info');
    const showAlert = (message, title = 'Alert', type = 'info') => {
        setMessage(message);
        setTitle(title);
        setAlertType(type);
        setIsOpen(true);
    };
    const handleClose = () => {
        setIsOpen(false);
    };
    // Get the appropriate Bootstrap variant based on alert type
    const getVariant = (type) => {
        switch (type) {
            case 'success': return 'success';
            case 'error': return 'danger';
            case 'warning': return 'warning';
            case 'info': return 'info';
            default: return 'primary';
        }
    };
    return (react_1.default.createElement(AlertContext.Provider, { value: { showAlert } },
        children,
        react_1.default.createElement(react_bootstrap_1.Modal, { show: isOpen, onHide: handleClose, centered: true },
            react_1.default.createElement(react_bootstrap_1.Modal.Header, { closeButton: true, className: `bg-${getVariant(alertType)} text-white` },
                react_1.default.createElement(react_bootstrap_1.Modal.Title, null, title)),
            react_1.default.createElement(react_bootstrap_1.Modal.Body, null, message),
            react_1.default.createElement(react_bootstrap_1.Modal.Footer, null,
                react_1.default.createElement(react_bootstrap_1.Button, { variant: getVariant(alertType), onClick: handleClose }, "OK")))));
};
exports.AlertProvider = AlertProvider;
const useAlert = () => {
    const context = (0, react_1.useContext)(AlertContext);
    if (!context)
        throw new Error('useAlert must be used within an AlertProvider');
    return context;
};
exports.useAlert = useAlert;
