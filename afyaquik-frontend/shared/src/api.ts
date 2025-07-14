
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

    const response = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
        credentials: 'include', // Moved from headers to correct location
    });

    if (!response.ok) {
        try {
            // Try to parse error response as JSON
            const errorText = await response.text();
            let errorMessage = response.statusText;

            if (errorText) {
                try {
                    const errorJson = JSON.parse(errorText);
                    errorMessage = errorJson.message || errorMessage;

                    // Handle 400 errors with toast if provided
                    if (response.status === 400 && showToast) {
                        showToast(errorMessage, 'error');
                    }
                } catch (parseError) {
                    // If JSON parsing fails, use the raw text
                    errorMessage = errorText;
                }
            }

            throw new Error(`Error ${response.status}: ${errorMessage}`);
        } catch (error) {
            // Re-throw the error we created or pass through any other errors
            throw error;
        }
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
