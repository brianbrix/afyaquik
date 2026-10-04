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
const StepForm_1 = __importDefault(require("../StepForm"));
const api_1 = __importDefault(require("../api"));
const react_1 = __importStar(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const ToastContext_1 = require("../ToastContext");
const NotificationService_1 = require("../communication/NotificationService");
const session_1 = require("../session");
const PatientAssignForm = ({ visitId }) => {
    const [stations, setStations] = (0, react_1.useState)([]);
    const [officers, setOfficers] = (0, react_1.useState)([]);
    const [selectedStation, setSelectedStation] = (0, react_1.useState)('');
    const formMethods = (0, react_1.useRef)(null);
    const [formValues] = (0, react_1.useState)({
        patientVisitId: visitId,
        nextStation: '',
        assignedOfficer: ''
    });
    const { showToast } = (0, ToastContext_1.useToast)();
    (0, react_1.useEffect)(() => {
        (0, api_1.default)("/stations", { method: "GET" })
            .then((data) => {
            const stationOptions = data.map((s) => ({ label: s.name, value: s.name, roles: s.allowedRoles || [] }));
            setStations(stationOptions);
        })
            .catch(error => showToast(error.message || 'Unable to load stations', 'error'));
    }, []);
    const back = function () {
        return (react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-info", className: "btn btn-success mb-4", onClick: () => window.location.href = `index.html#/visits/${visitId}/details` },
            react_1.default.createElement("i", { className: "bi bi-arrow-left me-1" }),
            " Back to Summary"));
    };
    (0, react_1.useEffect)(() => {
        let cancelled = false;
        setOfficers([]);
        if (!selectedStation)
            return;
        (0, api_1.default)(`/stations/${encodeURIComponent(selectedStation)}/users`, { method: "GET" })
            .then((users) => {
            if (cancelled)
                return;
            const officerOptions = users.map((user) => ({
                label: [user.firstName, user.secondName, user.lastName].filter(Boolean).join(' ') || user.username,
                value: user.username,
                roles: user.roles || []
            }));
            setOfficers(officerOptions);
        })
            .catch(error => { if (!cancelled)
            showToast(error.message || 'Unable to load officers', 'error'); });
        return () => { cancelled = true; };
    }, [selectedStation]);
    const handleFieldChange = (fieldName, value) => {
        var _a;
        if (fieldName === 'nextStation') {
            setSelectedStation(value);
            (_a = formMethods.current) === null || _a === void 0 ? void 0 : _a.setValue('assignedOfficer', '');
        }
    };
    const formConfig = [
        {
            label: "Assign Patient",
            fields: [
                {
                    name: "patientVisitId",
                    label: "Visit Identifier",
                    type: "text",
                    disabled: true, colSpan: 6
                },
                {
                    name: "nextStation",
                    label: "Next Station",
                    type: "select",
                    options: stations, colSpan: 6,
                    onChange: (val) => handleFieldChange('nextStation', val),
                    required: true
                },
                {
                    name: "assignedOfficer",
                    label: "Next Officer",
                    type: "select",
                    onChange: (val) => handleFieldChange('assignedOfficer', val),
                    options: officers, colSpan: 6, required: true
                }
            ],
            topComponents: [back()]
        }
    ];
    return (react_1.default.createElement(StepForm_1.default, { config: formConfig, onSubmit: (data) => __awaiter(void 0, void 0, void 0, function* () {
            const response = yield (0, api_1.default)('/patient/visits/assignments/create', { method: 'POST', body: data });
            const officer = officers.find(option => option.value === data.assignedOfficer);
            const station = stations.find(option => option.value === data.nextStation);
            const role = officer === null || officer === void 0 ? void 0 : officer.roles.find(candidate => station === null || station === void 0 ? void 0 : station.roles.includes(candidate));
            const portal = Object.keys(session_1.PORTAL_ROLES)
                .find(name => name !== 'auth' && session_1.PORTAL_ROLES[name].some(candidate => candidate === role));
            try {
                if (role && portal) {
                    yield (0, NotificationService_1.sendNotification)(response.assignedOfficerId, 'New Patient Alert', `You have been assigned a patient at ${response.nextStation}`, (0, session_1.portalUrl)(portal, `/visits/${visitId}/details`), 'VISIT', role);
                }
                else {
                    showToast('Handoff saved. This station has no supported notification portal.', 'warning');
                }
            }
            catch (_a) {
                showToast('Handoff saved, but the notification could not be sent.', 'warning');
            }
            window.location.href = `index.html#/visits/${visitId}/details`;
        }), idFromParent: visitId, defaultValues: formValues, formMethodsRef: formMethods, submitButtonLabel: 'Assign Patient' }));
};
exports.default = PatientAssignForm;
