
export interface ApiOptions {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
    body?: any;
    token?: string;
}
const BASE_URL = process.env.REACT_APP_API_URL || '/api';

export default async function apiRequest<T = any>(endpoint: string, options: ApiOptions = {}, showToast?:any): Promise<T> {
    const { method = 'GET', body } = options;
    const url = `${BASE_URL}${endpoint}`;

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        'credentials': 'include',
    };


    const response = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
        if(response.status === 400 ){
            if (showToast) {
                response.json().then(
                    (data) => {
                        showToast(data.message, 'error');
                    }
                )
            }
            else
            {
                return response.json();
            }
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        const errorText = await response.text();
        throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
    }

    // Check if response has JSON content
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    } else {
        // For non-JSON responses, return the response text
        const text = await response.text();
        // Try to parse as JSON if possible, otherwise return the text or an empty object
        try {
            return text ? JSON.parse(text) : {} as T;
        } catch (e) {
            // If parsing fails, return the text as is or an empty object if text is empty
            return (text || {}) as T;
        }
    }
}
