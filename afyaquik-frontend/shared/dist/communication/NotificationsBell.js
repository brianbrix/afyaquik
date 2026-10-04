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
const react_bootstrap_1 = require("react-bootstrap");
const dateFormatter_1 = __importDefault(require("../dateFormatter"));
const NotificationService_1 = require("./NotificationService");
const NotificationsBell = ({ userId, userRole }) => {
    const [notifications, setNotifications] = (0, react_1.useState)([]);
    (0, react_1.useEffect)(() => {
        if (localStorage.getItem('isLoggedIn') == 'true') {
            console.log("Checking notifications for userId:", userId, "with role:", userRole);
            (0, NotificationService_1.fetchNotifications)(setNotifications, userId, userRole);
            console.log("Notifications", notifications);
        }
    }, [userId, userRole]);
    return (react_1.default.createElement(react_bootstrap_1.Dropdown, null,
        react_1.default.createElement(react_bootstrap_1.Dropdown.Toggle, { variant: "light", id: "notifications-dropdown" },
            react_1.default.createElement("i", { className: "bi bi-bell-fill" }),
            notifications.length > 0 && (react_1.default.createElement(react_bootstrap_1.Badge, { bg: "danger", className: "ms-1" }, notifications.length))),
        react_1.default.createElement(react_bootstrap_1.Dropdown.Menu, { style: { minWidth: 320 } },
            react_1.default.createElement(react_bootstrap_1.Dropdown.Header, { className: "fw-semibold text-primary" }, "Notifications"),
            notifications.length === 0 ? (react_1.default.createElement(react_bootstrap_1.Dropdown.Item, { disabled: true }, "No new notifications")) : (notifications.filter((n) => (n.recipientRole === userRole)).map((n) => (react_1.default.createElement(react_bootstrap_1.Dropdown.Item, { key: n.id, onClick: () => {
                    (0, NotificationService_1.markAsRead)(n.id, setNotifications, userId, userRole);
                    if (n.targetUrl)
                        window.location.href = n.targetUrl;
                }, className: "text-wrap" },
                react_1.default.createElement("strong", null, n.title),
                react_1.default.createElement("div", { className: "small text-muted" }, n.message),
                react_1.default.createElement("div", { className: "small text-secondary" },
                    "Sent at: ",
                    (0, dateFormatter_1.default)(n.createdAt)))))),
            notifications.length > 0 && (react_1.default.createElement(react_bootstrap_1.Dropdown.Divider, null)),
            notifications.length > 0 && (react_1.default.createElement(react_bootstrap_1.Dropdown.Item, { className: "text-center text-primary", onClick: () => __awaiter(void 0, void 0, void 0, function* () {
                    (0, NotificationService_1.markAllAsRead)(setNotifications, userId, userRole);
                }) }, "Mark all as read")))));
};
exports.default = NotificationsBell;
