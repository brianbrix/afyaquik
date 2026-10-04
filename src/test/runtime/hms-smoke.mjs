import assert from 'node:assert/strict';

const base = process.env.HMS_BASE_URL || 'http://localhost:8081';
const username = process.env.HMS_TEST_USERNAME;
const password = process.env.HMS_TEST_PASSWORD;
assert.ok(username && password && process.env.HMS_ALLOW_SYNTHETIC_WRITES === 'yes',
    'Use an isolated database and set HMS_TEST_USERNAME, HMS_TEST_PASSWORD, and HMS_ALLOW_SYNTHETIC_WRITES=yes');

let checks = 0;
class Session {
    cookies = new Map();

    async request(route, method = 'GET', body, expected = 200, csrf = true) {
        if (csrf && !['GET', 'HEAD', 'OPTIONS'].includes(method)) await this.request('/auth/csrf');
        const response = await fetch(`${base}/api${route}`, {
            method,
            headers: {
                'User-Agent': 'AfyaQuik-Isolated-Smoke-Test',
                'Content-Type': 'application/json',
                Cookie: [...this.cookies].map(([name, value]) => `${name}=${value}`).join('; '),
                ...(csrf && this.cookies.has('XSRF-TOKEN') ? { 'X-XSRF-TOKEN': decodeURIComponent(this.cookies.get('XSRF-TOKEN')) } : {}),
            },
            body: body === undefined ? undefined : JSON.stringify(body),
        });
        for (const cookie of response.headers.getSetCookie()) {
            const [pair] = cookie.split(';');
            const separator = pair.indexOf('=');
            this.cookies.set(pair.slice(0, separator), pair.slice(separator + 1));
        }
        const text = await response.text();
        assert.equal(response.status, expected, `${method} ${route}: expected ${expected}, received ${response.status}: ${text.slice(0, 250)}`);
        checks++;
        return text ? JSON.parse(text) : null;
    }

    async login(name, credential) {
        return this.request('/auth/login', 'POST', { username: name, password: credential });
    }
}

const anonymous = new Session();
await anonymous.request('/users/me', 'GET', undefined, 401);
await anonymous.request('/auth/login', 'POST', { username, password }, 403, false);
await anonymous.request('/auth/login', 'POST', { username, password: 'NotTheTestPassword' }, 401);
await anonymous.request('/password-reset/request', 'POST', { username: 'nonexistent-smoke-account' });

const administrator = new Session();
await administrator.login(username, password);
const administratorDetails = await administrator.request('/users/me');
assert.ok(administratorDetails.roles.includes('SUPERADMIN'));
assert.ok(!('password' in administratorDetails) && !('passwordHash' in administratorDetails));
await administrator.request('/delete', 'POST', { entityName: 'users', ids: [1] }, 403);
await administrator.request('/unknown-route', 'GET', undefined, 403);
await administrator.request('/search', 'POST', { searchEntity: 'patients', page: 0, size: 10000 }, 400);

const suffix = Date.now().toString(36);
const staff = {};
for (const role of ['RECEPTIONIST', 'NURSE', 'DOCTOR', 'PHARMACIST', 'REPORTS', 'ADMIN']) {
    const name = `smoke.${role.toLowerCase()}.${suffix}`;
    const record = await administrator.request('/users', 'POST', {
        username: name, password, email: `${name}@example.invalid`, firstName: 'Synthetic', secondName: '',
        lastName: role, enabled: true, available: true, roles: [role],
        stations: role === 'NURSE' || role === 'DOCTOR' ? ['TRIAGE'] : role === 'PHARMACIST' ? ['PHARMACY'] : [],
    });
    const session = new Session();
    await session.login(name, password);
    staff[role] = { session, record, name };
}

const reception = staff.RECEPTIONIST.session;
await reception.request('/users', 'GET', undefined, 403);
await reception.request('/search', 'POST', { searchEntity: 'users', page: 0, size: 10 }, 403);
await reception.request('/reports/summary', 'GET', undefined, 403);
const patient = await reception.request('/patients', 'POST', {
    firstName: 'Synthetic', secondName: '', lastName: `Patient${suffix}`, gender: '', nationalId: '', maritalStatus: '',
    dateOfBirth: '2020-01-01', contactInfo: { phoneNumber: '0712345678', email: '', address: 'Nairobi - smoke test' },
});
assert.equal(patient.contactInfo.phoneNumber, '+254712345678');
assert.equal(patient.gender, null);
const visit = await reception.request(`/patients/${patient.id}/visits/create`, 'POST', {
    summaryReasonForVisit: 'Synthetic workflow verification', visitType: 'CONSULTATION',
});

const nurse = staff.NURSE.session;
await nurse.request('/users', 'GET', undefined, 403);
await nurse.request('/patients', 'POST', { firstName: 'ForbiddenWrite' }, 403);
const catalogue = await nurse.request('/patient/triage/items');
assert.ok(catalogue.length >= 8);
await nurse.request(`/patient/triage/${visit.id}/update`, 'PUT', [{ name: 'Temperature (C)', value: '37.2' }]);
await nurse.request(`/patient/triage/${visit.id}/update`, 'PUT', [{ name: 'Temperature (C)', value: '37.0' }]);
const triage = await nurse.request(`/patient/triage/${visit.id}?size=100`);
assert.equal(triage.results.content.length, 1);
assert.equal(triage.results.content[0].value, '37.0');
const handoff = await nurse.request('/patient/visits/assignments/create', 'POST', {
    patientVisitId: visit.id, nextStation: 'TRIAGE', assignedOfficer: staff.DOCTOR.name,
});
assert.equal(handoff.assignedOfficerId, staff.DOCTOR.record.id);
await nurse.request('/patient/visits/assignments/create', 'POST', {
    patientVisitId: visit.id, nextStation: 'PHARMACY', assignedOfficer: staff.DOCTOR.name,
}, 400);
await nurse.request(`/notifications/unread/${administratorDetails.id}?roleName=SUPERADMIN`, 'GET', undefined, 403);

await staff.DOCTOR.session.request(`/patient/triage/${visit.id}`);
await staff.PHARMACIST.session.request('/patient-drugs', 'POST', {}, 403);
await staff.REPORTS.session.request('/patients/1', 'GET', undefined, 403);
const summary = await staff.REPORTS.session.request('/reports/summary');
assert.ok(summary.registeredPatients >= 1 && summary.visits >= 1);
await staff.REPORTS.session.request('/reports/summary?from=2026-10-04&to=2026-10-01', 'GET', undefined, 400);
await staff.ADMIN.session.request('/users', 'POST', {
    username: `smoke.escalation.${suffix}`, password, email: `escalation.${suffix}@example.invalid`,
    firstName: 'Synthetic', secondName: '', lastName: 'Escalation', roles: ['SUPERADMIN'],
}, 403);

const stale = new Session();
stale.cookies = new Map(nurse.cookies);
await nurse.request('/auth/logout', 'POST');
await stale.request('/users/me', 'GET', undefined, 401);
console.log(`PASS: ${checks} HTTP checks plus workflow assertions. Synthetic patient ${patient.id}, visit ${visit.id}.`);