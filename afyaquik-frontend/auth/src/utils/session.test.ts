import { ApiError, apiRequest, clearSession, safePortalRedirect, saveSession, sessionRoles } from '@afyaquik/shared';

beforeEach(() => {
    localStorage.clear();
    global.fetch = jest.fn();
});

test.each([
    'https://external.example/client/auth/index.html',
    '//external.example/client/auth/index.html',
    'javascript://localhost/client/auth/index.html',
    '/api/users',
    'http://localhost:9099/client/admin/index.html',
])('rejects untrusted redirect %s', candidate => {
    expect(safePortalRedirect(candidate)).toBeNull();
});

test('preserves trusted portal deep links', () => {
    expect(safePortalRedirect('/client/doctor/index.html#/visits/42/details'))
        .toBe(`${window.location.origin}/client/doctor/index.html#/visits/42/details`);
});

test('malformed role storage fails closed', () => {
    localStorage.setItem('userRoles', '{broken');
    expect(sessionRoles()).toEqual([]);
    localStorage.setItem('userRoles', JSON.stringify(['NURSE', 1, null]));
    expect(sessionRoles()).toEqual(['NURSE']);
});

test('server-confirmed roles replace stale role and station selections', () => {
    localStorage.setItem('currentRole', 'SUPERADMIN');
    localStorage.setItem('formattedStations', 'ADMINISTRATION');
    saveSession(7, ['NURSE']);
    expect(sessionRoles()).toEqual(['NURSE']);
    expect(localStorage.getItem('currentRole')).toBe('NURSE');
    expect(localStorage.getItem('formattedStations')).toBeNull();
});

test('session cleanup does not erase unrelated preferences', () => {
    saveSession(7, ['NURSE']);
    localStorage.setItem('tablePageSize', '25');
    clearSession();
    expect(sessionRoles()).toEqual([]);
    expect(localStorage.getItem('isLoggedIn')).toBeNull();
    expect(localStorage.getItem('tablePageSize')).toBe('25');
});

test.each([401, 403])('preserves API error status %s', async status => {
    (global.fetch as jest.Mock).mockResolvedValue({ ok: false, status, text: async () => '{}' });
    await expect(apiRequest('/users/me')).rejects.toMatchObject({ status, name: 'ApiError' });
});

test('API errors retain their status and message', () => {
    const error = new ApiError('Access denied', 403);
    expect(error).toBeInstanceOf(Error);
    expect(error.message).toBe('Access denied');
    expect(error.status).toBe(403);
});