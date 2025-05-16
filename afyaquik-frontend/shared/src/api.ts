export interface ApiOptions {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
    body?: any;
    token?: string;
}
const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

function getToken() {
    return localStorage.getItem('authToken');
}
export default async function apiRequest<T = any>(endpoint: string, options: ApiOptions = {}): Promise<T> {
    const { method = 'GET', body } = options;
    const url = `${BASE_URL}${endpoint}`;
    const token = options.token ?? getToken();

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
    }

    return response.json();
}
