"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.VisitWorkflowActions = VisitWorkflowActions;
exports.default = EncounterOperations;
const react_1 = __importStar(require("react"));
const api_1 = __importDefault(require("../api"));
const session_1 = require("../session");
function useWorkspace(endpoint) {
    const [data, setData] = (0, react_1.useState)(null);
    const [error, setError] = (0, react_1.useState)('');
    const [busy, setBusy] = (0, react_1.useState)(false);
    const [attempt, setAttempt] = (0, react_1.useState)(0);
    (0, react_1.useEffect)(() => {
        let active = true;
        setData(null);
        setError('');
        setBusy(true);
        (0, api_1.default)(endpoint).then(value => { if (active)
            setData(value); })
            .catch(failure => { if (active)
            setError(failure.message || 'Unable to load this workspace.'); })
            .finally(() => { if (active)
            setBusy(false); });
        return () => { active = false; };
    }, [endpoint, attempt]);
    const update = (path, method, body) => __awaiter(this, void 0, void 0, function* () {
        setBusy(true);
        setError('');
        try {
            yield (0, api_1.default)(path, { method, body });
            setData(yield (0, api_1.default)(endpoint));
            return true;
        }
        catch (failure) {
            setError(failure instanceof Error ? failure.message : 'This action could not be completed.');
            return false;
        }
        finally {
            setBusy(false);
        }
    });
    return { data, error, busy, update, retry: () => setAttempt(value => value + 1) };
}
function WorkspaceError({ error, retry }) {
    return error ? react_1.default.createElement("div", { className: "alert alert-danger", role: "alert" },
        error,
        react_1.default.createElement("button", { className: "btn btn-link", onClick: retry, title: "Reload workspace" },
            react_1.default.createElement("i", { className: "bi bi-arrow-clockwise", "aria-hidden": "true" }),
            " Retry")) : null;
}
function VisitWorkflowActions({ visitId }) {
    var _a;
    const workspace = useWorkspace(`/patient/visits/${visitId}`);
    const roles = (0, session_1.sessionRoles)();
    const canComplete = roles.some(role => ['DOCTOR', 'ADMIN', 'SUPERADMIN'].includes(role));
    const canCancel = canComplete || roles.includes('RECEPTIONIST');
    const closed = workspace.data && ['COMPLETED', 'CANCELLED'].includes(workspace.data.visitStatus);
    const status = (next) => __awaiter(this, void 0, void 0, function* () {
        if (next === 'CANCELLED' && !window.confirm('Cancel this encounter and its unfinished station assignments? This cannot be undone here.'))
            return;
        yield workspace.update(`/patient/visits/status-update/${visitId}?status=${next}`, 'PATCH');
    });
    return react_1.default.createElement("section", { className: "mb-3", "aria-label": "Encounter status" },
        react_1.default.createElement("div", { className: "d-flex flex-wrap align-items-center gap-2" },
            react_1.default.createElement("a", { href: "#/visits", className: "btn btn-outline-secondary" },
                react_1.default.createElement("i", { className: "bi bi-arrow-left me-2", "aria-hidden": "true" }),
                "Visits"),
            react_1.default.createElement("strong", null, ((_a = workspace.data) === null || _a === void 0 ? void 0 : _a.visitStatus.replace(/_/g, ' ')) || 'Loading status...'),
            !closed && workspace.data && react_1.default.createElement(react_1.default.Fragment, null,
                react_1.default.createElement("button", { className: "btn btn-outline-primary", disabled: workspace.busy || workspace.data.visitStatus === 'IN_PROGRESS', onClick: () => status('IN_PROGRESS') }, "Start Care"),
                canComplete && react_1.default.createElement("button", { className: "btn btn-success", disabled: workspace.busy || workspace.data.inpatientActive, onClick: () => status('COMPLETED') }, "Complete Encounter"),
                canCancel && react_1.default.createElement("button", { className: "btn btn-outline-danger", disabled: workspace.busy || workspace.data.inpatientActive, onClick: () => status('CANCELLED') }, "Cancel Encounter"))),
        react_1.default.createElement(WorkspaceError, { error: workspace.error, retry: workspace.retry }));
}
function CommunicationPanel({ visitId }) {
    const workspace = useWorkspace(`/patient-communications/visit/${visitId}`);
    const [consent, setConsent] = (0, react_1.useState)(false);
    const [language, setLanguage] = (0, react_1.useState)('EN');
    const [consentSource, setConsentSource] = (0, react_1.useState)('VERBAL');
    const [source, setSource] = (0, react_1.useState)('');
    const [clientReference, setClientReference] = (0, react_1.useState)(() => crypto.randomUUID());
    (0, react_1.useEffect)(() => { if (workspace.data) {
        setConsent(workspace.data.smsConsent);
        setLanguage(workspace.data.language);
    } }, [workspace.data]);
    const data = workspace.data;
    return react_1.default.createElement("section", { className: "py-3 border-top", "aria-labelledby": `communications-${visitId}` },
        react_1.default.createElement("h3", { id: `communications-${visitId}`, className: "h5" }, "Patient Communication"),
        react_1.default.createElement(WorkspaceError, { error: workspace.error, retry: workspace.retry }),
        !data && !workspace.error && react_1.default.createElement("p", { role: "status" }, "Loading communication history..."),
        data && react_1.default.createElement(react_1.default.Fragment, null,
            react_1.default.createElement("form", { className: "row g-3 align-items-end mb-3", onSubmit: (event) => __awaiter(this, void 0, void 0, function* () {
                    event.preventDefault();
                    yield workspace.update(`/patient-communications/patients/${data.patientId}/preferences`, 'PUT', { smsConsent: consent, language, consentSource });
                }) },
                react_1.default.createElement("div", { className: "col-12" },
                    react_1.default.createElement("strong", null, "Mobile:"),
                    " ",
                    data.phoneNumber || 'Not recorded'),
                react_1.default.createElement("label", { className: "col-sm-5" },
                    react_1.default.createElement("input", { type: "checkbox", checked: consent, disabled: workspace.busy, onChange: event => setConsent(event.target.checked), className: "form-check-input me-2" }),
                    "SMS consent confirmed"),
                react_1.default.createElement("label", { className: "col-sm-3" },
                    "Language",
                    react_1.default.createElement("select", { className: "form-select", value: language, onChange: event => setLanguage(event.target.value) },
                        react_1.default.createElement("option", { value: "EN" }, "English"),
                        react_1.default.createElement("option", { value: "SW" }, "Kiswahili"))),
                react_1.default.createElement("label", { className: "col-sm-4" },
                    "Consent record",
                    react_1.default.createElement("select", { className: "form-select", value: consentSource, onChange: event => setConsentSource(event.target.value) },
                        react_1.default.createElement("option", { value: "VERBAL" }, "Verbal consent"),
                        react_1.default.createElement("option", { value: "WRITTEN" }, "Written consent"))),
                react_1.default.createElement("div", { className: "col-12" },
                    react_1.default.createElement("button", { disabled: workspace.busy, className: "btn btn-outline-primary", type: "submit" }, "Save Preferences"))),
            !data.providerConfigured && react_1.default.createElement("div", { className: "alert alert-warning" }, "SMS sending is disabled. Provider configuration is required."),
            react_1.default.createElement("form", { className: "d-flex flex-wrap align-items-end gap-2 mb-3", onSubmit: (event) => __awaiter(this, void 0, void 0, function* () {
                    event.preventDefault();
                    const selected = data.sources.find(option => `${option.template}:${option.id}` === source);
                    if (!selected)
                        return;
                    if (yield workspace.update(`/patient-communications/patients/${data.patientId}/messages`, 'POST', { clientReference, template: selected.template, sourceId: selected.id }))
                        setClientReference(crypto.randomUUID());
                }) },
                react_1.default.createElement("label", { className: "flex-grow-1" },
                    "Message record",
                    react_1.default.createElement("select", { className: "form-select", required: true, value: source, onChange: event => { setSource(event.target.value); setClientReference(crypto.randomUUID()); } },
                        react_1.default.createElement("option", { value: "" }, data.sources.length ? 'Select a record' : 'No eligible records'),
                        data.sources.map(option => react_1.default.createElement("option", { key: `${option.template}:${option.id}`, value: `${option.template}:${option.id}` }, option.label)))),
                react_1.default.createElement("button", { className: "btn btn-primary", disabled: workspace.busy || !data.smsConsent || !source, type: "submit" }, "Queue Message")),
            react_1.default.createElement("div", { className: "table-responsive" },
                react_1.default.createElement("table", { className: "table align-middle" },
                    react_1.default.createElement("thead", null,
                        react_1.default.createElement("tr", null,
                            react_1.default.createElement("th", null, "Recipient"),
                            react_1.default.createElement("th", null, "Message"),
                            react_1.default.createElement("th", null, "Status"),
                            react_1.default.createElement("th", null, "Actions"))),
                    react_1.default.createElement("tbody", null,
                        data.messages.map(message => react_1.default.createElement("tr", { key: message.id },
                            react_1.default.createElement("td", null, message.recipient),
                            react_1.default.createElement("td", { style: { minWidth: 180, maxWidth: 440, overflowWrap: 'anywhere' } },
                                message.content,
                                react_1.default.createElement("small", { className: "d-block text-muted" },
                                    "Queued by ",
                                    message.queuedBy)),
                            react_1.default.createElement("td", null,
                                message.status === 'ACCEPTED' ? 'Accepted, delivery unconfirmed' : message.status,
                                react_1.default.createElement("small", { className: "d-block text-danger" }, message.failure)),
                            react_1.default.createElement("td", null, message.status === 'QUEUED' && react_1.default.createElement("div", { className: "d-flex gap-2" },
                                react_1.default.createElement("button", { className: "btn btn-outline-success", title: "Send SMS", "aria-label": "Send SMS", disabled: workspace.busy || !data.providerConfigured || !data.smsConsent, onClick: () => { if (window.confirm(`Send this SMS to ${message.recipient}?`))
                                        workspace.update(`/patient-communications/messages/${message.id}/send`, 'POST'); } },
                                    react_1.default.createElement("i", { className: "bi bi-send", "aria-hidden": "true" })),
                                react_1.default.createElement("button", { className: "btn btn-outline-danger", title: "Cancel queued message", "aria-label": "Cancel queued message", disabled: workspace.busy, onClick: () => workspace.update(`/patient-communications/messages/${message.id}/cancel`, 'POST') },
                                    react_1.default.createElement("i", { className: "bi bi-x-circle", "aria-hidden": "true" })))))),
                        !data.messages.length && react_1.default.createElement("tr", null,
                            react_1.default.createElement("td", { colSpan: 4 }, "No messages recorded.")))))));
}
function InpatientPanel({ visitId, onChange }) {
    const workspace = useWorkspace(`/inpatient/visit/${visitId}`);
    const [bedId, setBedId] = (0, react_1.useState)('');
    const [reason, setReason] = (0, react_1.useState)('');
    const [summary, setSummary] = (0, react_1.useState)('');
    const [ward, setWard] = (0, react_1.useState)('');
    const [bedCode, setBedCode] = (0, react_1.useState)('');
    const data = workspace.data;
    const active = data === null || data === void 0 ? void 0 : data.admissions.find(admission => admission.status !== 'DISCHARGED');
    const update = (path, body) => __awaiter(this, void 0, void 0, function* () { if (yield workspace.update(path, 'POST', body)) {
        setBedId('');
        setReason('');
        setSummary('');
        onChange();
    } });
    return react_1.default.createElement("section", { className: "py-3 border-top", "aria-labelledby": `inpatient-${visitId}` },
        react_1.default.createElement("h3", { id: `inpatient-${visitId}`, className: "h5" }, "Inpatient Care"),
        react_1.default.createElement(WorkspaceError, { error: workspace.error, retry: workspace.retry }),
        !data && !workspace.error && react_1.default.createElement("p", { role: "status" }, "Loading admission and bed status..."),
        data && react_1.default.createElement(react_1.default.Fragment, null,
            active ? react_1.default.createElement("p", null,
                react_1.default.createElement("strong", null,
                    active.ward,
                    " / ",
                    active.bed),
                " ",
                react_1.default.createElement("span", { className: "badge bg-secondary" }, active.status.replace(/_/g, ' '))) : react_1.default.createElement("p", null, "No active admission for this visit."),
            data.visitOpen && ((!active && data.canOrder) || (active && (data.canOrder || data.canDischarge))) && react_1.default.createElement("form", { className: "row g-3 align-items-end mb-3", onSubmit: event => {
                    event.preventDefault();
                    update(active ? `/inpatient/admissions/${active.id}/transfer` : '/inpatient/admissions', { visitId, bedId: Number(bedId), reason });
                } },
                react_1.default.createElement("label", { className: "col-sm-5" },
                    active ? 'Transfer to bed' : 'Admission bed',
                    react_1.default.createElement("select", { className: "form-select", required: true, value: bedId, onChange: event => setBedId(event.target.value) },
                        react_1.default.createElement("option", { value: "" }, "Select available bed"),
                        data.beds.filter(bed => bed.active && !bed.occupied).map(bed => react_1.default.createElement("option", { key: bed.id, value: bed.id },
                            bed.ward,
                            " / ",
                            bed.code)))),
                react_1.default.createElement("label", { className: "col-sm-7" },
                    active ? 'Transfer reason' : 'Admission reason',
                    react_1.default.createElement("input", { className: "form-control", required: true, minLength: 5, maxLength: 1000, value: reason, onChange: event => setReason(event.target.value) })),
                react_1.default.createElement("div", { className: "col-12" },
                    react_1.default.createElement("button", { type: "submit", className: "btn btn-primary", disabled: workspace.busy }, active ? 'Transfer Patient' : 'Admit Patient'))),
            (active === null || active === void 0 ? void 0 : active.status) === 'ADMITTED' && data.canOrder && react_1.default.createElement("form", { className: "mb-3", onSubmit: event => { event.preventDefault(); update(`/inpatient/admissions/${active.id}/discharge-order`, { summary }); } },
                react_1.default.createElement("label", { className: "d-block" },
                    "Discharge summary",
                    react_1.default.createElement("textarea", { className: "form-control mb-2", required: true, minLength: 20, maxLength: 4000, rows: 3, value: summary, onChange: event => setSummary(event.target.value) })),
                react_1.default.createElement("button", { className: "btn btn-outline-primary", disabled: workspace.busy, type: "submit" }, "Order Discharge")),
            (active === null || active === void 0 ? void 0 : active.status) === 'DISCHARGE_ORDERED' && react_1.default.createElement("div", { className: "mb-3" },
                react_1.default.createElement("p", null, active.dischargeSummary),
                data.canDischarge && react_1.default.createElement("button", { className: "btn btn-success", disabled: workspace.busy, onClick: () => { if (window.confirm('Confirm the patient has departed and release this bed?'))
                        update(`/inpatient/admissions/${active.id}/discharge`); } }, "Record Discharge")),
            data.canManageBeds && react_1.default.createElement("details", { className: "mb-3" },
                react_1.default.createElement("summary", null, "Bed Setup"),
                react_1.default.createElement("form", { className: "row g-2 mt-1", onSubmit: (event) => __awaiter(this, void 0, void 0, function* () {
                        event.preventDefault();
                        if (yield workspace.update('/inpatient/beds', 'POST', { ward, code: bedCode })) {
                            setWard('');
                            setBedCode('');
                        }
                    }) },
                    react_1.default.createElement("label", { className: "col-sm-5" },
                        "Ward",
                        react_1.default.createElement("input", { className: "form-control", required: true, minLength: 2, maxLength: 80, value: ward, onChange: event => setWard(event.target.value) })),
                    react_1.default.createElement("label", { className: "col-sm-5" },
                        "Bed code",
                        react_1.default.createElement("input", { className: "form-control", required: true, maxLength: 30, value: bedCode, onChange: event => setBedCode(event.target.value) })),
                    react_1.default.createElement("div", { className: "col-sm-2 align-self-end" },
                        react_1.default.createElement("button", { className: "btn btn-outline-primary", type: "submit", disabled: workspace.busy }, "Add Bed")))),
            data.admissions.map(admission => react_1.default.createElement("details", { key: admission.id, className: "mb-2" },
                react_1.default.createElement("summary", null,
                    "Admission ",
                    admission.id,
                    ": ",
                    admission.status.replace(/_/g, ' '),
                    " (",
                    admission.ward,
                    " / ",
                    admission.bed,
                    ")"),
                admission.admissionReason && react_1.default.createElement("p", { className: "mt-2" }, admission.admissionReason),
                admission.dischargeSummary && react_1.default.createElement("p", null, admission.dischargeSummary),
                react_1.default.createElement("ul", null, admission.events.map((event, index) => react_1.default.createElement("li", { key: index },
                    event.occurredAt.replace('T', ' ').slice(0, 16),
                    " EAT: ",
                    event.action.replace(/_/g, ' '),
                    event.toBed ? ` to ${event.toBed}` : '',
                    ", ",
                    event.actor,
                    ". ",
                    event.reason)))))));
}
function EncounterOperations({ visitId, includeVisitActions = true }) {
    const [version, setVersion] = (0, react_1.useState)(0);
    return react_1.default.createElement("div", null,
        includeVisitActions && react_1.default.createElement(VisitWorkflowActions, { key: version, visitId: visitId }),
        react_1.default.createElement(CommunicationPanel, { visitId: visitId }),
        react_1.default.createElement(InpatientPanel, { visitId: visitId, onChange: () => setVersion(value => value + 1) }));
}
