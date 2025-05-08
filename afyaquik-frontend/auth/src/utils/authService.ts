export const authService = {
    login: async (username: string, password: string) => {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        const data = await response.json();
        if (response.ok) {
            const rolesResponse = await fetch('/api/users/me', {
                headers: { 'Authorization': `Bearer ${data.token}` }
            });
            const rolesData = await rolesResponse.json();
            if (rolesResponse.ok) {
                data.roles = rolesData.roles;
            }
            localStorage.setItem('userRoles', JSON.stringify(data.roles));
            localStorage.setItem('authToken', data.token);
        }
        return data;
    },
    logout: () => {
        localStorage.removeItem('authToken');
    },
    getToken: () => {
        return localStorage.getItem('authToken');
    },
    isLoggedIn: () => {
        return !!localStorage.getItem('authToken');
    }
};
