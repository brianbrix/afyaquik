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
exports.markAllAsRead = exports.markAsRead = exports.fetchNotifications = exports.sendNotification = void 0;
const api_1 = __importDefault(require("../api"));
const sendNotification = (recipientId, title, message, targetUrl, type, recipientRole) => __awaiter(void 0, void 0, void 0, function* () {
    const requestBody = {
        recipientId: recipientId,
        title: title,
        message: message,
        targetUrl: targetUrl,
        type: type,
        recipientRole: recipientRole
    };
    const response = yield (0, api_1.default)(`/notifications/send`, { method: 'POST', body: requestBody });
    return response;
});
exports.sendNotification = sendNotification;
const fetchNotifications = (setNotifications, userId, roleName) => __awaiter(void 0, void 0, void 0, function* () {
    const data = yield (0, api_1.default)(`/notifications/unread/${userId}?roleName=${roleName}`);
    setNotifications(data);
});
exports.fetchNotifications = fetchNotifications;
const markAsRead = (id, setNotifications, userId, roleName) => __awaiter(void 0, void 0, void 0, function* () {
    yield (0, api_1.default)(`/notifications/mark-read/${id}`, { method: 'PUT' });
    (0, exports.fetchNotifications)(setNotifications, userId, roleName);
});
exports.markAsRead = markAsRead;
const markAllAsRead = (setNotifications, userId, roleName) => __awaiter(void 0, void 0, void 0, function* () {
    yield (0, api_1.default)(`/notifications/mark-all-read/${userId}?roleName=${roleName}`, { method: 'PUT' });
    (0, exports.fetchNotifications)(setNotifications, userId, roleName);
});
exports.markAllAsRead = markAllAsRead;
