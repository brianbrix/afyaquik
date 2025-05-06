import { fetchUtils } from 'react-admin';
import simpleRestProvider from 'ra-data-simple-rest';

const apiUrl = 'http://localhost:8080/api';
const httpClient = (url: string, options: any = {}) => {
    const token = localStorage.getItem('authToken');
    if (!options.headers) {
        options.headers = new Headers({ Accept: 'application/json' });
    }
    if (token) {
        options.headers.set('Authorization', `Bearer ${token}`);
    }
    return fetchUtils.fetchJson(url, options);
};

export default simpleRestProvider(apiUrl, httpClient);
