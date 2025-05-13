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

const resourceUrlMap: { [key: string]: string } = {
    users: 'users',
    roles: 'roles',
    stations: 'stations',
    generalSettings: 'settings/general',
};
const dataProvider = {
    ...baseDataProvider,

    getList: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource];
        console.log("Resource", resource);
        console.log("Endpoint", endpoint);
        let url = `${apiUrl}/${endpoint}`;

        // Check if pagination param exists and whether it's explicitly disabled
        const shouldPaginate = params?.pagination && params.pagination !== false;

        if (shouldPaginate) {
            const { page, perPage } = params.pagination;
            url += `?page=${page - 1}&size=${perPage}`;
        }

        return httpClient(url).then(({ json }) => {
            // Handle both array and paginated response shapes
            const data = Array.isArray(json) ? json : json.content || json;
            const total = Array.isArray(json)
                ? json.length
                : json.totalElements || (json.content ? json.content.length : 0);
            return { data, total };
        });
    }
};

export default dataProvider;
