import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { apiRequest, DataTable, EncounterOperations, StepConfig, StepForm, useToast } from '@afyaquik/shared';

interface TriageItem {
    id: number;
    name: string;
}

interface Visit {
    id: number;
    patientName: string;
    visitStatus: string;
    summaryReasonForVisit?: string;
}

const NUMERIC_VITALS = new Set(['Weight (kg)', 'Height (cm)', 'Temperature (C)', 'Systolic BP (mmHg)',
    'Diastolic BP (mmHg)', 'Pulse (beats/min)', 'Respiratory rate (breaths/min)', 'Oxygen saturation (%)']);

export function NursingQueue() {
    const [status, setStatus] = useState('active');
    return <main className="container py-4">
        <div className="d-flex flex-wrap align-items-center justify-content-between gap-3">
            <h1 className="h3 mb-0">Nursing Queue</h1>
            <label className="d-flex align-items-center gap-2">Visits
                <select className="form-select" value={status} onChange={event => setStatus(event.target.value)}>
                    <option value="active">Active</option><option value="all">All visits</option>
                    <option value="COMPLETED">Completed</option><option value="CANCELLED">Cancelled</option>
                </select>
            </label>
        </div>
        <DataTable<Visit> key={status} title="Patients" dataEndpoint="/search" requestMethod="POST" searchEntity="visits"
            dateFieldName="visitDate" combinedSearchFieldsAndTerms={status === 'active'
                ? 'visitStatus=STARTED||PENDING||IN_PROGRESS' : status === 'all' ? undefined : `visitStatus=${status}`}
            columns={[{ header: 'Visit', accessor: 'id' }, { header: 'Patient', accessor: 'patientName' },
                { header: 'Date', accessor: 'visitDate', type: 'date' }, { header: 'Reason', accessor: 'summaryReasonForVisit' },
                { header: 'Status', accessor: 'visitStatus' }]}
            searchFields={[{ name: 'patient.firstName', label: 'First name' }, { name: 'patient.lastName', label: 'Last name' }]}
            detailsView="index.html#/visits/#id/triage" detailsTitle="Record Vitals" detailsClassName="bi bi-heart-pulse"
            showMultipleDeleteButton={false} />
    </main>;
}

export function TriagePage() {
    const { id } = useParams();
    const [visit, setVisit] = useState<Visit | null>(null);
    const [items, setItems] = useState<TriageItem[]>([]);
    const [values, setValues] = useState<{ measurements: string[] }>({ measurements: [] });
    const [error, setError] = useState('');
    const [attempt, setAttempt] = useState(0);
    const { showToast } = useToast();

    useEffect(() => {
        let active = true;
        setError('');
        setVisit(null);
        Promise.all([apiRequest(`/patient/visits/${id}`), apiRequest('/patient/triage/items'),
            apiRequest(`/patient/triage/${id}?size=100`)]).then(([details, definitions, report]) => {
            if (!active) return;
            const saved = new Map<string, string>((report.results?.content || []).map((item: { name: string; value: string }) => [item.name, item.value]));
            setItems(definitions);
            setValues({ measurements: definitions.map((item: TriageItem) => saved.get(item.name) || '') });
            setVisit(details);
        }).catch(() => { if (active) setError('Unable to load this visit and its vital signs.'); });
        return () => { active = false; };
    }, [id, attempt]);

    if (error) return <main className="container py-4"><div className="alert alert-danger" role="alert">{error}</div>
        <button className="btn btn-primary" onClick={() => setAttempt(value => value + 1)}><i className="bi bi-arrow-clockwise me-2" />Retry</button></main>;
    if (!visit) return <main className="container py-4" role="status">Loading visit...</main>;
    const config: StepConfig[] = [{ label: 'Vital Signs', fields: items.map((item, index) => ({
        name: `measurements.${index}`, label: item.name, type: NUMERIC_VITALS.has(item.name) ? 'number' : 'text',
        step: NUMERIC_VITALS.has(item.name) ? 'any' : undefined, min: NUMERIC_VITALS.has(item.name) ? 0 : undefined,
        max: item.name === 'Oxygen saturation (%)' ? 100 : undefined, colSpan: 6,
    })) }];

    return <main className="container py-4">
        <div className="d-flex flex-wrap justify-content-between gap-3 mb-3">
            <Link to="/visits" className="btn btn-outline-secondary"><i className="bi bi-arrow-left me-2" />Queue</Link>
            {!['COMPLETED', 'CANCELLED'].includes(visit.visitStatus) && <Link to={`/visits/${id}/assign`} className="btn btn-outline-primary"><i className="bi bi-person-plus me-2" />Handoff</Link>}
        </div>
        <h1 className="h3">{visit.patientName}</h1>
        <p className="text-muted">Visit {visit.id} · {visit.visitStatus.replace(/_/g, ' ')}</p>
        {visit.summaryReasonForVisit && <p>{visit.summaryReasonForVisit}</p>}
        {['COMPLETED', 'CANCELLED'].includes(visit.visitStatus) ? <div className="alert alert-secondary">This encounter is closed.</div> : items.length ? <StepForm config={config} defaultValues={values} submitButtonLabel="Save Vital Signs"
            onSubmit={async data => {
                const measurements = items.map((item, index) => ({ name: item.name, value: String(data.measurements?.[index] ?? '').trim() }))
                    .filter(item => item.value !== '');
                if (!measurements.length) throw new Error('Enter at least one measurement.');
                await apiRequest(`/patient/triage/${id}/update`, { method: 'PUT', body: measurements });
                showToast('Vital signs saved.', 'success');
            }} /> : <div className="alert alert-warning">No triage measurements are configured. Contact your facility administrator.</div>}
        <EncounterOperations visitId={visit.id} />
    </main>;
}