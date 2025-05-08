export const authService = {
    login: async (username: string, password: string) => {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        const data = await response.json();
        if (response.ok) {
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
