
/**
 * Interface for API request options
 */
export interface ApiOptions {
    /** HTTP method for the request */
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
    /** Request body (will be JSON stringified) */
    body?: any;
    /** Authentication token (if needed) */
    token?: string;
}

/** Base URL for API requests */
const BASE_URL = process.env.REACT_APP_API_URL || '/api';

let pendingCsrfToken: Promise<string> | null = null;

export class ApiError extends Error {
    constructor(message: string, public readonly status: number) {
        super(message);
        this.name = 'ApiError';
    }
}

export async function csrfHeaders(method = 'GET', existing?: HeadersInit): Promise<Headers> {
    const headers = new Headers(existing);
    if (['GET', 'HEAD', 'OPTIONS'].includes(method.toUpperCase())) return headers;
    const cookie = document.cookie.split('; ').find(value => value.startsWith('XSRF-TOKEN='));
    let token = cookie ? decodeURIComponent(cookie.substring('XSRF-TOKEN='.length)) : '';
    if (!token) {
        if (!pendingCsrfToken) {
            pendingCsrfToken = fetch(`${BASE_URL}/auth/csrf`, { credentials: 'include' })
                .then(async response => {
                    if (!response.ok) throw new ApiError('Unable to establish a secure session.', response.status);
                    const result = await response.json();
                    if (!result.token) throw new Error('Missing session security token.');
                    return result.token as string;
                }).finally(() => { pendingCsrfToken = null; });
        }
        token = await pendingCsrfToken;
    }
    headers.set('X-XSRF-TOKEN', token);
    return headers;
}

export async function fetchWithCsrf(url: string, options: RequestInit = {}): Promise<Response> {
    return fetch(url, {
        ...options,
        credentials: 'include',
        headers: await csrfHeaders(options.method, options.headers),
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
export default async function apiRequest<T = any>(endpoint: string, options: ApiOptions = {}, showToast?: (message: string, type: string) => void): Promise<T> {
    const { method = 'GET', body } = options;
    const url = `${BASE_URL}${endpoint}`;

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
    };

    const response = await fetchWithCsrf(url, {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined,
        credentials: 'include', // Moved from headers to correct location
    });

    if (!response.ok) {
        let message = response.status === 401 ? 'Your session has expired. Please sign in again.'
            : response.status === 403 ? 'You do not have permission for this action.'
            : 'The request could not be completed.';
        const errorText = await response.text();
        try {
            const error = JSON.parse(errorText);
            message = error.message || message;
        } catch {
            if (errorText && !errorText.includes('<')) message = errorText;
        }
        showToast?.(message, 'error');
        throw new ApiError(message, response.status);
    }

    // Check if response has JSON content
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    }

    // For non-JSON responses
    const text = await response.text();
    if (!text) {
        return {} as T;
    }

    // Try to parse as JSON if possible, otherwise return the text
    try {
        return JSON.parse(text);
    } catch (e) {
        return text as unknown as T;
    }
}
