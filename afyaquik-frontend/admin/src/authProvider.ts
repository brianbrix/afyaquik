import {apiRequest} from "@afyaquik/shared";

const authProvider = {
    login: ({ username, password }: any) => {
        const request = new Request('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
            headers: new Headers({ 'Content-Type': 'application/json' }),
        });
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText);
                }
                localStorage.setItem('isLoggedIn', String(true));
                return response.json();
            })
            .then((response ) => {
                apiRequest('/api/users/me').then(
                    rolesResponse => rolesResponse.json()
                    .then((rolesData: { roles: any; }) => {
                        localStorage.setItem('userRoles', JSON.stringify(rolesData.roles));
                    })
                )

            });
    },
    logout: () => {
        apiRequest('/auth/logout',{
            method: 'POST',
            body: JSON.stringify({})
        }).then(() => {
            console.log("Logged out");
            localStorage.clear();
        });
        return Promise.resolve();
    },
    checkAuth: () =>
        localStorage.getItem('isLoggedIn') ? Promise.resolve() : Promise.reject(),
    checkError: (error: any) => {
        if (error.status === 401 || error.status === 403) {
            localStorage.removeItem('isLoggedIn');
            return Promise.reject();
        }
        return Promise.resolve();
    },
    getPermissions: () => Promise.resolve(),
};

export default authProvider;
