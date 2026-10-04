import React, { useEffect, useState } from 'react';
import { apiRequest } from '@afyaquik/shared';
import Papa from 'papaparse';

interface Summary {
    from: string;
    to: string;
    registeredPatients: number;
    visits: number;
    appointments: number;
    prescriptions: number;
    inpatientAdmissions: number;
    inpatientDischarges: number;
    visitsByStatus: Record<string, number>;
    appointmentsByStatus: Record<string, number>;
}

const today = new Date().toLocaleDateString('en-CA', { timeZone: 'Africa/Nairobi' });

function Dhis2ExportPanel() {
    const [configuration, setConfiguration] = useState<{ ready: boolean; mappingVersion: string; errors: string[] } | null>(null);
    const [month, setMonth] = useState(today.substring(0, 7));
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);
    useEffect(() => {
        let active = true;
        apiRequest('/reports/dhis2/config').then(value => { if (active) setConfiguration(value); })
            .catch(failure => { if (active) setError(failure.message || 'Unable to load DHIS2 configuration.'); });
        return () => { active = false; };
    }, []);
    const download = async () => {
        setBusy(true); setError('');
        try {
            const payload = await apiRequest(`/reports/dhis2/export?period=${month.replace('-', '')}`);
            const url = URL.createObjectURL(new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' }));
            const link = document.createElement('a');
            link.href = url; link.hidden = true; link.download = `afyaquik-dhis2-draft-${month}.json`;
            document.body.appendChild(link); link.click();
            setTimeout(() => { link.remove(); URL.revokeObjectURL(url); }, 1000);
        } catch (failure) { setError(failure instanceof Error ? failure.message : 'Unable to export DHIS2 draft.'); }
        finally { setBusy(false); }
    };
    return <section className="border-top pt-4 mt-4" aria-labelledby="dhis2-title">
        <h2 id="dhis2-title" className="h5">DHIS2 / HMIS Draft</h2>
        {error && <div className="alert alert-danger" role="alert">{error}</div>}
        {configuration?.ready === false && <div className="alert alert-warning"><ul className="mb-0">{(configuration.errors || []).map(message => <li key={message}>{message}</li>)}</ul></div>}
        {configuration?.ready && <p>Mapping version: {configuration.mappingVersion}</p>}
        <form className="d-flex flex-wrap align-items-end gap-3" onSubmit={event => { event.preventDefault(); download(); }}>
            <label>Reporting month<input className="form-control" type="month" value={month} max={today.substring(0, 7)} min="2010-01" required onChange={event => setMonth(event.target.value)} /></label>
            <button className="btn btn-outline-primary" disabled={!configuration?.ready || busy} type="submit"><i className="bi bi-download me-2" aria-hidden="true" />Export DHIS2 Draft</button>
        </form>
    </section>;
}

export default function ReportsWorkspace() {
    const [from, setFrom] = useState(`${today.substring(0, 7)}-01`);
    const [to, setTo] = useState(today);
    const [period, setPeriod] = useState({ from, to });
    const [summary, setSummary] = useState<Summary | null>(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [attempt, setAttempt] = useState(0);

    useEffect(() => {
        let active = true;
        setLoading(true);
        setError('');
        apiRequest<Summary>(`/reports/summary?from=${period.from}&to=${period.to}`).then(result => {
            if (active) setSummary(result);
        }).catch(failure => { if (active) setError(failure.message || 'Unable to load reports.'); })
            .finally(() => { if (active) setLoading(false); });
        return () => { active = false; };
    }, [period.from, period.to, attempt]);

    const download = () => {
        if (!summary) return;
        const rows = [
            { metric: 'Registered patients', count: summary.registeredPatients }, { metric: 'Visits', count: summary.visits },
            { metric: 'Appointments', count: summary.appointments }, { metric: 'Prescriptions recorded', count: summary.prescriptions },
            { metric: 'Inpatient admissions', count: summary.inpatientAdmissions ?? 0 }, { metric: 'Inpatient discharges', count: summary.inpatientDischarges ?? 0 },
            ...Object.entries(summary.visitsByStatus).map(([status, count]) => ({ metric: `Visits: ${status}`, count })),
            ...Object.entries(summary.appointmentsByStatus).map(([status, count]) => ({ metric: `Appointments: ${status}`, count })),
        ].map(row => ({ from: summary.from, to: summary.to, ...row }));
        const url = URL.createObjectURL(new Blob([Papa.unparse(rows, { escapeFormulae: true })], { type: 'text/csv;charset=utf-8' }));
        const link = document.createElement('a');
        link.href = url;
        link.download = `afyaquik-activity-${summary.from}-${summary.to}.csv`;
        link.hidden = true;
        document.body.appendChild(link);
        link.click();
        setTimeout(() => {
            link.remove();
            URL.revokeObjectURL(url);
        }, 1000);
    };

    return <main className="container py-4">
        <div className="d-flex flex-wrap justify-content-between gap-3 mb-4">
            <h1 className="h3 mb-0">Facility Activity</h1>
            <button className="btn btn-outline-primary" disabled={!summary || loading || !!error} onClick={download}>
                <i className="bi bi-download me-2" />Export CSV</button>
        </div>
        <form className="d-flex flex-wrap gap-3 align-items-end border-bottom pb-4 mb-4" onSubmit={event => {
            event.preventDefault();
            if (to < from) { setError('The end date must not precede the start date.'); return; }
            setPeriod({ from, to });
            setAttempt(value => value + 1);
        }}>
            <div><label className="form-label" htmlFor="report-from">From</label><input className="form-control" id="report-from" type="date" value={from} max={to} required onChange={event => setFrom(event.target.value)} /></div>
            <div><label className="form-label" htmlFor="report-to">To</label><input className="form-control" id="report-to" type="date" value={to} min={from} required onChange={event => setTo(event.target.value)} /></div>
            <button className="btn btn-primary" type="submit" disabled={loading}><i className="bi bi-arrow-clockwise me-2" />Refresh</button>
        </form>
        {error && <div className="alert alert-danger" role="alert">{error}</div>}
        {loading && <p role="status">Loading activity...</p>}
        {summary && !loading && !error && <>
            <dl className="row g-4 border-bottom pb-4 mb-4">
                {[['Registered patients', summary.registeredPatients], ['Visits', summary.visits], ['Appointments', summary.appointments],
                    ['Prescriptions recorded', summary.prescriptions], ['Inpatient admissions', summary.inpatientAdmissions ?? 0],
                    ['Inpatient discharges', summary.inpatientDischarges ?? 0]].map(([label, count]) => <div className="col-6 col-lg-4" key={label}>
                    <dt className="small text-muted fw-normal">{label}</dt><dd className="h3 mt-2 mb-0">{count.toLocaleString('en-KE')}</dd></div>)}
            </dl>
            <div className="row g-4">
                {[['Visits', summary.visitsByStatus], ['Appointments', summary.appointmentsByStatus]].map(([title, counts]) => <section className="col-12 col-md-6" key={title as string}>
                    <h2 className="h5">{title as string}</h2><table className="table"><thead><tr><th>Status</th><th className="text-end">Count</th></tr></thead>
                        <tbody>{Object.entries(counts as Record<string, number>).map(([status, count]) => <tr key={status}>
                            <td>{status.replace(/_/g, ' ')}</td><td className="text-end">{count.toLocaleString('en-KE')}</td></tr>)}</tbody></table>
                    {!Object.keys(counts).length && <p className="text-muted">No activity in this period.</p>}
                </section>)}
            </div>
        </>}
        <Dhis2ExportPanel />
    </main>;
}