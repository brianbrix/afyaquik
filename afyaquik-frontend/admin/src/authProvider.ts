import {apiRequest, clearSession, saveSession, sessionRoles} from "@afyaquik/shared";

const authProvider = {
    login: async ({ username, password }: any) => {
        const result = await apiRequest('/auth/login', { method: 'POST', body: { username, password } });
        if (!result.roles?.some((role: string) => ['ADMIN', 'SUPERADMIN'].includes(role))) {
            throw new Error('Administrator access is required.');
        }
        saveSession(result.userId, result.roles);
    },
    logout: async () => {
        await apiRequest('/auth/logout', { method: 'POST', body: {} });
        clearSession();
    },
    checkAuth: async () => {
        const user = await apiRequest('/users/me');
        if (!user.roles?.some((role: string) => ['ADMIN', 'SUPERADMIN'].includes(role))) {
            throw new Error('Administrator access is required.');
        }
        saveSession(user.id, user.roles);
    },
    checkError: (error: any) => {
        if (error.status === 401) {
            clearSession();
            return Promise.reject();
        }
        if (error.status === 403) return Promise.reject({ logoutUser: false });
        return Promise.resolve();
    },
    getPermissions: () => Promise.resolve(sessionRoles()),
};

export default authProvider;
