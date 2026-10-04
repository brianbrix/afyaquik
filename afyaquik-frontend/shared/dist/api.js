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
Object.defineProperty(exports, "__esModule", { value: true });
exports.ApiError = void 0;
exports.csrfHeaders = csrfHeaders;
exports.fetchWithCsrf = fetchWithCsrf;
exports.default = apiRequest;
/** Base URL for API requests */
const BASE_URL = process.env.REACT_APP_API_URL || '/api';
let pendingCsrfToken = null;
class ApiError extends Error {
    constructor(message, status) {
        super(message);
        this.status = status;
        this.name = 'ApiError';
    }
}
exports.ApiError = ApiError;
function csrfHeaders() {
    return __awaiter(this, arguments, void 0, function* (method = 'GET', existing) {
        const headers = new Headers(existing);
        if (['GET', 'HEAD', 'OPTIONS'].includes(method.toUpperCase()))
            return headers;
        const cookie = document.cookie.split('; ').find(value => value.startsWith('XSRF-TOKEN='));
        let token = cookie ? decodeURIComponent(cookie.substring('XSRF-TOKEN='.length)) : '';
        if (!token) {
            if (!pendingCsrfToken) {
                pendingCsrfToken = fetch(`${BASE_URL}/auth/csrf`, { credentials: 'include' })
                    .then((response) => __awaiter(this, void 0, void 0, function* () {
                    if (!response.ok)
                        throw new ApiError('Unable to establish a secure session.', response.status);
                    const result = yield response.json();
                    if (!result.token)
                        throw new Error('Missing session security token.');
                    return result.token;
                })).finally(() => { pendingCsrfToken = null; });
            }
            token = yield pendingCsrfToken;
        }
        headers.set('X-XSRF-TOKEN', token);
        return headers;
    });
}
function fetchWithCsrf(url_1) {
    return __awaiter(this, arguments, void 0, function* (url, options = {}) {
        return fetch(url, Object.assign(Object.assign({}, options), { credentials: 'include', headers: yield csrfHeaders(options.method, options.headers) }));
    });
}
/**
 * Makes an API request to the specified endpoint
 *
 * @param endpoint - The API endpoint to call (will be appended to BASE_URL)
 * @param options - Request options (method, body, etc.)
 * @param showToast - Optional toast function to display error messages
 * @returns Promise with the response data
 */
function apiRequest(endpoint_1) {
    return __awaiter(this, arguments, void 0, function* (endpoint, options = {}, showToast) {
        const { method = 'GET', body } = options;
        const url = `${BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
        };
        const response = yield fetchWithCsrf(url, {
            method,
            headers,
            body: body !== undefined ? JSON.stringify(body) : undefined,
            credentials: 'include', // Moved from headers to correct location
        });
        if (!response.ok) {
            let message = response.status === 401 ? 'Your session has expired. Please sign in again.'
                : response.status === 403 ? 'You do not have permission for this action.'
                    : 'The request could not be completed.';
            const errorText = yield response.text();
            try {
                const error = JSON.parse(errorText);
                message = error.message || message;
            }
            catch (_a) {
                if (errorText && !errorText.includes('<'))
                    message = errorText;
            }
            showToast === null || showToast === void 0 ? void 0 : showToast(message, 'error');
            throw new ApiError(message, response.status);
        }
        // Check if response has JSON content
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        // For non-JSON responses
        const text = yield response.text();
        if (!text) {
            return {};
        }
        // Try to parse as JSON if possible, otherwise return the text
        try {
            return JSON.parse(text);
        }
        catch (e) {
            return text;
        }
    });
}
