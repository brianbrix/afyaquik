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
exports.useToast = exports.ToastProvider = void 0;
const react_1 = __importStar(require("react"));
const ToastContext = (0, react_1.createContext)(undefined);
const ToastProvider = ({ children }) => {
    const [toast, setToast] = (0, react_1.useState)(null);
    const [isVisible, setIsVisible] = (0, react_1.useState)(false);
    const showToast = (message, type = 'info') => {
        setToast({ message, type });
        setIsVisible(true);
        setTimeout(() => {
            setIsVisible(false);
            setTimeout(() => setToast(null), 300); // Wait for fade-out to complete
        }, 5000);
    };
    return (react_1.default.createElement(ToastContext.Provider, { value: { showToast } },
        children,
        toast && (react_1.default.createElement("div", { style: {
                position: 'fixed',
                top: '6%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                padding: '16px 24px',
                borderRadius: '8px',
                color: toast.type === 'warning' ? '#212529' : 'white',
                fontWeight: 500,
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
                zIndex: 1000,
                opacity: isVisible ? 1 : 0,
                transition: 'opacity 0.3s ease-in-out',
                display: 'flex',
                alignItems: 'center',
                gap: '16px'
            }, className: `toast-${toast.type}` },
            toast.message,
            react_1.default.createElement("button", { style: {
                    background: 'transparent',
                    border: 'none',
                    color: 'inherit',
                    cursor: 'pointer',
                    fontSize: '1.2rem',
                    padding: 0
                }, onClick: () => {
                    setIsVisible(false);
                    setTimeout(() => setToast(null), 300);
                } }, "\u00D7")))));
};
exports.ToastProvider = ToastProvider;
const useToast = () => {
    const context = (0, react_1.useContext)(ToastContext);
    if (!context)
        throw new Error('useToast must be used within a ToastProvider');
    return context;
};
exports.useToast = useToast;
