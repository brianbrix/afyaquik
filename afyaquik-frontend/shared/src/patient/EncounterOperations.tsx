import React, { useEffect, useState } from 'react';
import apiRequest from '../api';
import { sessionRoles } from '../session';

function useWorkspace<T>(endpoint: string) {
    const [data, setData] = useState<T | null>(null);
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);
    const [attempt, setAttempt] = useState(0);
    useEffect(() => {
        let active = true;
        setData(null); setError(''); setBusy(true);
        apiRequest(endpoint).then(value => { if (active) setData(value); })
            .catch(failure => { if (active) setError(failure.message || 'Unable to load this workspace.'); })
            .finally(() => { if (active) setBusy(false); });
        return () => { active = false; };
    }, [endpoint, attempt]);
    const update = async (path: string, method: 'POST' | 'PUT' | 'PATCH', body?: unknown) => {
        setBusy(true); setError('');
        try {
            await apiRequest(path, { method, body });
            setData(await apiRequest(endpoint));
            return true;
        } catch (failure) { setError(failure instanceof Error ? failure.message : 'This action could not be completed.'); return false; }
        finally { setBusy(false); }
    };
    return { data, error, busy, update, retry: () => setAttempt(value => value + 1) };
}

function WorkspaceError({ error, retry }: { error: string; retry: () => void }) {
    return error ? <div className="alert alert-danger" role="alert">{error}<button className="btn btn-link" onClick={retry} title="Reload workspace"><i className="bi bi-arrow-clockwise" aria-hidden="true" /> Retry</button></div> : null;
}

export function VisitWorkflowActions({ visitId }: { visitId: number }) {
    const workspace = useWorkspace<{ visitStatus: string; inpatientActive: boolean }>(`/patient/visits/${visitId}`);
    const roles = sessionRoles();
    const canComplete = roles.some(role => ['DOCTOR', 'ADMIN', 'SUPERADMIN'].includes(role));
    const canCancel = canComplete || roles.includes('RECEPTIONIST');
    const closed = workspace.data && ['COMPLETED', 'CANCELLED'].includes(workspace.data.visitStatus);
    const status = async (next: string) => {
        if (next === 'CANCELLED' && !window.confirm('Cancel this encounter and its unfinished station assignments? This cannot be undone here.')) return;
        await workspace.update(`/patient/visits/status-update/${visitId}?status=${next}`, 'PATCH');
    };
    return <section className="mb-3" aria-label="Encounter status">
        <div className="d-flex flex-wrap align-items-center gap-2">
            <a href="#/visits" className="btn btn-outline-secondary"><i className="bi bi-arrow-left me-2" aria-hidden="true" />Visits</a>
            <strong>{workspace.data?.visitStatus.replace(/_/g, ' ') || 'Loading status...'}</strong>
            {!closed && workspace.data && <>
                <button className="btn btn-outline-primary" disabled={workspace.busy || workspace.data.visitStatus === 'IN_PROGRESS'} onClick={() => status('IN_PROGRESS')}>Start Care</button>
                {canComplete && <button className="btn btn-success" disabled={workspace.busy || workspace.data.inpatientActive} onClick={() => status('COMPLETED')}>Complete Encounter</button>}
                {canCancel && <button className="btn btn-outline-danger" disabled={workspace.busy || workspace.data.inpatientActive} onClick={() => status('CANCELLED')}>Cancel Encounter</button>}
            </>}
        </div>
        <WorkspaceError error={workspace.error} retry={workspace.retry} />
    </section>;
}

interface Message { id: number; recipient: string; content: string; status: string; failure: string | null; queuedBy: string; }
interface Communications { patientId: number; smsConsent: boolean; language: string; phoneNumber: string; providerConfigured: boolean; messages: Message[]; sources: { template: string; id: number; label: string }[]; }

function CommunicationPanel({ visitId }: { visitId: number }) {
    const workspace = useWorkspace<Communications>(`/patient-communications/visit/${visitId}`);
    const [consent, setConsent] = useState(false);
    const [language, setLanguage] = useState('EN');
    const [consentSource, setConsentSource] = useState('VERBAL');
    const [source, setSource] = useState('');
    const [clientReference, setClientReference] = useState(() => crypto.randomUUID());
    useEffect(() => { if (workspace.data) { setConsent(workspace.data.smsConsent); setLanguage(workspace.data.language); } }, [workspace.data]);
    const data = workspace.data;
    return <section className="py-3 border-top" aria-labelledby={`communications-${visitId}`}>
        <h3 id={`communications-${visitId}`} className="h5">Patient Communication</h3>
        <WorkspaceError error={workspace.error} retry={workspace.retry} />
        {!data && !workspace.error && <p role="status">Loading communication history...</p>}
        {data && <>
            <form className="row g-3 align-items-end mb-3" onSubmit={async event => {
                event.preventDefault();
                await workspace.update(`/patient-communications/patients/${data.patientId}/preferences`, 'PUT', { smsConsent: consent, language, consentSource });
            }}>
                <div className="col-12"><strong>Mobile:</strong> {data.phoneNumber || 'Not recorded'}</div>
                <label className="col-sm-5"><input type="checkbox" checked={consent} disabled={workspace.busy} onChange={event => setConsent(event.target.checked)} className="form-check-input me-2" />SMS consent confirmed</label>
                <label className="col-sm-3">Language<select className="form-select" value={language} onChange={event => setLanguage(event.target.value)}><option value="EN">English</option><option value="SW">Kiswahili</option></select></label>
                <label className="col-sm-4">Consent record<select className="form-select" value={consentSource} onChange={event => setConsentSource(event.target.value)}><option value="VERBAL">Verbal consent</option><option value="WRITTEN">Written consent</option></select></label>
                <div className="col-12"><button disabled={workspace.busy} className="btn btn-outline-primary" type="submit">Save Preferences</button></div>
            </form>
            {!data.providerConfigured && <div className="alert alert-warning">SMS sending is disabled. Provider configuration is required.</div>}
            <form className="d-flex flex-wrap align-items-end gap-2 mb-3" onSubmit={async event => {
                event.preventDefault();
                const selected = data.sources.find(option => `${option.template}:${option.id}` === source);
                if (!selected) return;
                if (await workspace.update(`/patient-communications/patients/${data.patientId}/messages`, 'POST', { clientReference, template: selected.template, sourceId: selected.id })) setClientReference(crypto.randomUUID());
            }}>
                <label className="flex-grow-1">Message record<select className="form-select" required value={source} onChange={event => { setSource(event.target.value); setClientReference(crypto.randomUUID()); }}>
                    <option value="">{data.sources.length ? 'Select a record' : 'No eligible records'}</option>
                    {data.sources.map(option => <option key={`${option.template}:${option.id}`} value={`${option.template}:${option.id}`}>{option.label}</option>)}
                </select></label>
                <button className="btn btn-primary" disabled={workspace.busy || !data.smsConsent || !source} type="submit">Queue Message</button>
            </form>
            <div className="table-responsive"><table className="table align-middle"><thead><tr><th>Recipient</th><th>Message</th><th>Status</th><th>Actions</th></tr></thead><tbody>
                {data.messages.map(message => <tr key={message.id}><td>{message.recipient}</td><td style={{ minWidth: 180, maxWidth: 440, overflowWrap: 'anywhere' }}>{message.content}<small className="d-block text-muted">Queued by {message.queuedBy}</small></td>
                    <td>{message.status === 'ACCEPTED' ? 'Accepted, delivery unconfirmed' : message.status}<small className="d-block text-danger">{message.failure}</small></td>
                    <td>{message.status === 'QUEUED' && <div className="d-flex gap-2">
                        <button className="btn btn-outline-success" title="Send SMS" aria-label="Send SMS" disabled={workspace.busy || !data.providerConfigured || !data.smsConsent} onClick={() => { if (window.confirm(`Send this SMS to ${message.recipient}?`)) workspace.update(`/patient-communications/messages/${message.id}/send`, 'POST'); }}><i className="bi bi-send" aria-hidden="true" /></button>
                        <button className="btn btn-outline-danger" title="Cancel queued message" aria-label="Cancel queued message" disabled={workspace.busy} onClick={() => workspace.update(`/patient-communications/messages/${message.id}/cancel`, 'POST')}><i className="bi bi-x-circle" aria-hidden="true" /></button>
                    </div>}</td></tr>)}
                {!data.messages.length && <tr><td colSpan={4}>No messages recorded.</td></tr>}
            </tbody></table></div>
        </>}
    </section>;
}

interface Admission { id: number; status: string; ward: string; bed: string; admittedAt: string; admissionReason: string | null; dischargeSummary: string | null; events: { action: string; fromBed: string | null; toBed: string | null; occurredAt: string; actor: string; reason: string }[]; }
interface InpatientWorkspace { admissions: Admission[]; beds: { id: number; ward: string; code: string; active: boolean; occupied: boolean }[]; canOrder: boolean; canDischarge: boolean; canManageBeds: boolean; visitOpen: boolean; }

function InpatientPanel({ visitId, onChange }: { visitId: number; onChange: () => void }) {
    const workspace = useWorkspace<InpatientWorkspace>(`/inpatient/visit/${visitId}`);
    const [bedId, setBedId] = useState('');
    const [reason, setReason] = useState('');
    const [summary, setSummary] = useState('');
    const [ward, setWard] = useState('');
    const [bedCode, setBedCode] = useState('');
    const data = workspace.data;
    const active = data?.admissions.find(admission => admission.status !== 'DISCHARGED');
    const update = async (path: string, body?: unknown) => { if (await workspace.update(path, 'POST', body)) { setBedId(''); setReason(''); setSummary(''); onChange(); } };
    return <section className="py-3 border-top" aria-labelledby={`inpatient-${visitId}`}>
        <h3 id={`inpatient-${visitId}`} className="h5">Inpatient Care</h3>
        <WorkspaceError error={workspace.error} retry={workspace.retry} />
        {!data && !workspace.error && <p role="status">Loading admission and bed status...</p>}
        {data && <>
            {active ? <p><strong>{active.ward} / {active.bed}</strong> <span className="badge bg-secondary">{active.status.replace(/_/g, ' ')}</span></p> : <p>No active admission for this visit.</p>}
            {data.visitOpen && ((!active && data.canOrder) || (active && (data.canOrder || data.canDischarge))) && <form className="row g-3 align-items-end mb-3" onSubmit={event => {
                event.preventDefault();
                update(active ? `/inpatient/admissions/${active.id}/transfer` : '/inpatient/admissions', { visitId, bedId: Number(bedId), reason });
            }}>
                <label className="col-sm-5">{active ? 'Transfer to bed' : 'Admission bed'}<select className="form-select" required value={bedId} onChange={event => setBedId(event.target.value)}>
                    <option value="">Select available bed</option>{data.beds.filter(bed => bed.active && !bed.occupied).map(bed => <option key={bed.id} value={bed.id}>{bed.ward} / {bed.code}</option>)}
                </select></label>
                <label className="col-sm-7">{active ? 'Transfer reason' : 'Admission reason'}<input className="form-control" required minLength={5} maxLength={1000} value={reason} onChange={event => setReason(event.target.value)} /></label>
                <div className="col-12"><button type="submit" className="btn btn-primary" disabled={workspace.busy}>{active ? 'Transfer Patient' : 'Admit Patient'}</button></div>
            </form>}
            {active?.status === 'ADMITTED' && data.canOrder && <form className="mb-3" onSubmit={event => { event.preventDefault(); update(`/inpatient/admissions/${active.id}/discharge-order`, { summary }); }}>
                <label className="d-block">Discharge summary<textarea className="form-control mb-2" required minLength={20} maxLength={4000} rows={3} value={summary} onChange={event => setSummary(event.target.value)} /></label>
                <button className="btn btn-outline-primary" disabled={workspace.busy} type="submit">Order Discharge</button>
            </form>}
            {active?.status === 'DISCHARGE_ORDERED' && <div className="mb-3"><p>{active.dischargeSummary}</p>{data.canDischarge && <button className="btn btn-success" disabled={workspace.busy} onClick={() => { if (window.confirm('Confirm the patient has departed and release this bed?')) update(`/inpatient/admissions/${active.id}/discharge`); }}>Record Discharge</button>}</div>}
            {data.canManageBeds && <details className="mb-3"><summary>Bed Setup</summary><form className="row g-2 mt-1" onSubmit={async event => {
                event.preventDefault(); if (await workspace.update('/inpatient/beds', 'POST', { ward, code: bedCode })) { setWard(''); setBedCode(''); }
            }}><label className="col-sm-5">Ward<input className="form-control" required minLength={2} maxLength={80} value={ward} onChange={event => setWard(event.target.value)} /></label>
                <label className="col-sm-5">Bed code<input className="form-control" required maxLength={30} value={bedCode} onChange={event => setBedCode(event.target.value)} /></label>
                <div className="col-sm-2 align-self-end"><button className="btn btn-outline-primary" type="submit" disabled={workspace.busy}>Add Bed</button></div>
            </form></details>}
            {data.admissions.map(admission => <details key={admission.id} className="mb-2"><summary>Admission {admission.id}: {admission.status.replace(/_/g, ' ')} ({admission.ward} / {admission.bed})</summary>
                {admission.admissionReason && <p className="mt-2">{admission.admissionReason}</p>}
                {admission.dischargeSummary && <p>{admission.dischargeSummary}</p>}
                <ul>{admission.events.map((event, index) => <li key={index}>{event.occurredAt.replace('T', ' ').slice(0, 16)} EAT: {event.action.replace(/_/g, ' ')}{event.toBed ? ` to ${event.toBed}` : ''}, {event.actor}. {event.reason}</li>)}</ul>
            </details>)}
        </>}
    </section>;
}

export default function EncounterOperations({ visitId, includeVisitActions = true }: { visitId: number; includeVisitActions?: boolean }) {
    const [version, setVersion] = useState(0);
    return <div>
        {includeVisitActions && <VisitWorkflowActions key={version} visitId={visitId} />}
        <CommunicationPanel visitId={visitId} />
        <InpatientPanel visitId={visitId} onChange={() => setVersion(value => value + 1)} />
    </div>;
}