
export interface ApiOptions {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
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
        if(response.status === 400 && showToast ){
            response.json().then(
                (data) => {
                    showToast(data.message, 'error');
                }
            )
        }
        const errorText = await response.text();
        throw new Error(`Error ${response.status}: ${errorText || response.statusText}`);
    }

    return response.json();
}
