import { apiRequest, clearSession, saveSession } from '@afyaquik/shared';

const resetData = () => ({ roles: [] as string[], isLoggedIn: false, userId: 0 });
let data = resetData();

export const authService = {

    login: async (username: string, password: string) => {
        data = resetData();
        const loginData = await apiRequest('/auth/login', {
            method: 'POST',
            body: { username, password },
        });
        if (loginData.isLoggedIn === true) {
            data.roles = Array.isArray(loginData.roles) ? [...loginData.roles] : [];
            data.userId = loginData.userId || 0;
            data.isLoggedIn = true;
            saveSession(data.userId, data.roles);
        }
        return data;
    },
    logout: async () => {
        await apiRequest('/auth/logout', { method: 'POST', body: {} });
        data = resetData();
        clearSession();
    },
    getToken: () => {
        return localStorage.getItem('isLoggedIn');
    },
    isLoggedIn: () => {
        return data.isLoggedIn;
    }
};
