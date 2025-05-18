import {apiRequest} from "@afyaquik/shared";

const data = {
    roles: undefined,
    isLoggedIn: false
};
export const authService = {

    login: async (username: string, password: string) => {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        if (response.ok) {
            data.isLoggedIn = true;
            const rolesData = await apiRequest('/users/me');
            data.roles = rolesData.roles;

            localStorage.setItem('userRoles', JSON.stringify(data.roles));
            localStorage.setItem('isLoggedIn', String(true));
        }
        return data;
    },
    logout: () => {
        apiRequest('/auth/logout',{
            method: 'POST',
            body: JSON.stringify({})
        }).then(() => {
            console.log("Logged out");
            localStorage.clear();
        });
    },
    getToken: () => {
        return localStorage.getItem('isLoggedIn');
    },
    isLoggedIn: () => {
        return data.isLoggedIn;
    }
};
