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
export declare class ApiError extends Error {
    readonly status: number;
    constructor(message: string, status: number);
}
export declare function csrfHeaders(method?: string, existing?: HeadersInit): Promise<Headers>;
export declare function fetchWithCsrf(url: string, options?: RequestInit): Promise<Response>;
/**
 * Makes an API request to the specified endpoint
 *
 * @param endpoint - The API endpoint to call (will be appended to BASE_URL)
 * @param options - Request options (method, body, etc.)
 * @param showToast - Optional toast function to display error messages
 * @returns Promise with the response data
 */
export default function apiRequest<T = any>(endpoint: string, options?: ApiOptions, showToast?: (message: string, type: string) => void): Promise<T>;
