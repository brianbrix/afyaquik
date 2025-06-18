import { fetchUtils } from 'react-admin';
import simpleRestProvider from 'ra-data-simple-rest';

const apiUrl = '/api';
const httpClient = (url: string, options: any = {}) => {
    if (!options.headers) {
        options.headers = new Headers({ Accept: 'application/json', credential: 'include' });
    }

    return fetchUtils.fetchJson(url, options);
};

const baseDataProvider = simpleRestProvider(apiUrl, httpClient);

const resourceUrlMap: { [key: string]: string } = {
    users: 'users',
    roles: 'roles',
    stations: 'stations',
    triageItems:'patient/triage/items',
    generalSettings: 'settings/general',
    observationItems: 'observation/items',
    observationItemCategories: 'observation/items/categories',
    treatmentPlanItems: 'plan/items',
    drugCategories: 'drugs/categories',
    drugForms: 'drugs/forms',
    drugs: 'drugs',
    drugInventory: 'drugs/inventory',
};
const dataProvider = {
    ...baseDataProvider,

        getList: (resource: string, params: any) => {
            const url = `${apiUrl}/search`;

            const shouldPaginate = params?.pagination !== false;

            const { page = 1, perPage = 10 } = shouldPaginate ? params.pagination : {};
            const { field = 'createdAt', order = 'DESC' } = params.sort || {};

            const requestBody: any = {
                searchEntity: resource,
                sort: `${field},${order}`
            };

            if (shouldPaginate) {
                requestBody.page = page - 1;
                requestBody.size = perPage;
            }

            if (params.filter?.q) {
                requestBody.query = params.filter.q;
            }

            if (params.filter?.searchFields) {
                requestBody.searchFields = params.filter.searchFields;
            }

            if (params.filter?.createdAt) {
                requestBody.createdAt = params.filter.createdAt;
            }

            return httpClient(url, {
                method: 'POST',
                body: JSON.stringify(requestBody),
            }).then(({ json }) => {
                const results = json.results || {};
                return {
                    data: results.content || [],
                    total: results.page?.totalElements || (Array.isArray(results) ? results.length : 0),
                };
            });
        },
        getMany: (resource: string, params: any) => {
            const url = `${apiUrl}/search`;

            const requestBody: any = {
                searchEntity: resource,
                sort: 'createdAt,DESC',
                query: params.ids.join(','),
                searchFields: ['id'],
                page: 0,
                operator:'equals',
                size: params.ids.length || 100
            };

            return httpClient(url, {
                method: 'POST',
                body: JSON.stringify(requestBody),
            }).then(({ json }) => {
                const results = json.results || {};
                return {
                    data: results.content || [],
                    total: results.page?.totalElements || (Array.isArray(results) ? results.length : 0),
                };
            });
        },
    create: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource] || resource;
        const url = `${apiUrl}/${endpoint}`;
        return httpClient(url, {
            method: 'POST',
            body: JSON.stringify(params.data),
        }).then(({ json }) => ({
            data: { ...params.data, id: json.id },
        }));
    },
    update: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource] || resource;
        const url = `${apiUrl}/${endpoint}/${params.id}`;
        return httpClient(url, {
            method: 'PUT',  // or 'PATCH' if your API supports partial updates
            body: JSON.stringify(params.data),
        }).then(({ json }) => ({
            data: json,
        }));
    },
    getOne: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource] || resource;
        const url = `${apiUrl}/${endpoint}/${params.id}`;
        return httpClient(url).then(({ json }) => ({
            data: json,
        }));
    },

    delete: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource] || resource;
        const url = `${apiUrl}/${endpoint}/${params.id}`;
        return httpClient(url, {
            method: 'DELETE',
        }).then(({ json }) => ({
            data: json || { id: params.id },  // Some APIs don't return content on DELETE
        }));
    },
    deleteMany: (resource: string, params: any) => {
        const endpoint = resourceUrlMap[resource] || resource;
        const url = `${apiUrl}/${endpoint}/${params.ids.join(',')}`;

        return httpClient(url, {
            method: 'DELETE',
            body: JSON.stringify(params.ids), // Send IDs as an array
        }).then(() => ({
            data: params.ids, // Return the deleted IDs to update the UI
        }));
    },

};

export default dataProvider;
