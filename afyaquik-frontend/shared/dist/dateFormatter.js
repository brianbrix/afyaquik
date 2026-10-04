"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = formatDate;
exports.formatForDatetimeLocal = formatForDatetimeLocal;
exports.formatJustDate = formatJustDate;
const dayjs_1 = __importDefault(require("dayjs"));
function formatDate(value, isDate) {
    if (!value)
        return 'N/A';
    const date = new Date(value);
    if (isNaN(date.getTime()))
        return value;
    if (isDate) {
        return (0, dayjs_1.default)(date).format('DD MMM YYYY');
    }
    return (0, dayjs_1.default)(date).format('DD MMM YYYY, hh:mm A');
}
function formatForDatetimeLocal(date) {
    const pad = (n) => n.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
function formatJustDate(date) {
    const pad = (n) => n.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}
