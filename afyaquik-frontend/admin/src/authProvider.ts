const authProvider = {
    login: ({ username, password }: any) => {
        const request = new Request('http://localhost:8080/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
            headers: new Headers({ 'Content-Type': 'application/json' }),
        });
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then((response ) => {
                localStorage.setItem('authToken', response);
                fetch('/api/users/me', {
                    headers: { 'Authorization': `Bearer ${response.token}` }
                }).then(
                    rolesResponse => rolesResponse.json()
                    .then(rolesData => {
                        localStorage.setItem('userRoles', JSON.stringify(rolesData.roles));
                    })
                )

            });
    },
    logout: () => {
        localStorage.clear()
        return Promise.resolve();
    },
    checkAuth: () =>
        localStorage.getItem('authToken') ? Promise.resolve() : Promise.reject(),
    checkError: (error: any) => {
        if (error.status === 401 || error.status === 403) {
            localStorage.removeItem('authToken');
            return Promise.reject();
        }
        return Promise.resolve();
    },
    getPermissions: () => Promise.resolve(),
};

export default authProvider;
