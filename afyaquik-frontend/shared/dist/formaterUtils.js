"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.formatCurrency = formatCurrency;
exports.formatNumber = formatNumber;
exports.formatPercentage = formatPercentage;
exports.formatWysiwyg = formatWysiwyg;
const react_1 = __importDefault(require("react"));
function formatCurrency(value) {
    if (typeof value === 'number') {
        return value.toLocaleString('en-US', { style: 'currency', currency: 'KES' });
    }
    return value;
}
function formatNumber(value) {
    if (typeof value === 'number') {
        return value.toLocaleString('en-US');
    }
    return value;
}
function formatPercentage(value) {
    if (typeof value === 'number') {
        return `${value.toFixed(2)}%`;
    }
    return value;
}
function formatWysiwyg(value) {
    return react_1.default.createElement('div', { dangerouslySetInnerHTML: { __html: value } });
}
