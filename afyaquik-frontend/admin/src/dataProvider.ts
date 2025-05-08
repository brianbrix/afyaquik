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

const baseDataProvider = simpleRestProvider(apiUrl, httpClient);

const dataProvider = {
    ...baseDataProvider,

    getList: (resource: string, params: any) => {
        if (resource === 'users') {
            const { page, perPage } = params.pagination;
            const url = `${apiUrl}/${resource}?page=${page - 1}&size=${perPage}`;

            return httpClient(url).then(({ json }) => ({
                data: json.content,
                total: json.page.totalElements,
            }));
        }

        // Fallback to default for other resources
        return baseDataProvider.getList(resource, params);
    }
};

export default dataProvider;
