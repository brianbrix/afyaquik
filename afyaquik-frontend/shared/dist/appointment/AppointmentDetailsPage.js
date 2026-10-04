"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_bootstrap_1 = require("react-bootstrap");
const react_1 = __importDefault(require("react"));
const index_1 = require("../index");
const AppointmentDetailsPage = ({ appointmentId, canEdit = true }) => {
    const endpoint = `/appointments/${appointmentId}`;
    const back = function () {
        return (react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-primary", className: "btn btn-success mb-4", onClick: () => window.location.href = "index.html#/appointments" },
            react_1.default.createElement("i", { className: "bi bi-arrow-left me-1" }),
            " Back To Appointment List"));
    };
    const editButton = function () {
        return (react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-warning", className: "btn btn-secondary mb-4", onClick: () => window.location.href = `index.html#/appointments/${appointmentId}/edit` },
            react_1.default.createElement("i", { className: "bi bi-pencil-fill me-1" }),
            " Edit Appointment"));
    };
    const fields = [
        { label: "Date scheduled", accessor: "appointmentDateTime", type: 'datetime' },
        { label: "Status", accessor: "status" },
        { label: "Reason", accessor: "reason" },
        { label: "First Name", accessor: "patient.firstName" },
        { label: "Last Name", accessor: "patient.lastName" },
        { label: "Email", accessor: "patient.contactInfo.email" },
        { label: "Phone", accessor: "patient.contactInfo.phoneNumber" },
        { label: "Gender", accessor: "patient.gender" },
        { label: "Doctor", accessor: "doctor.username" },
    ];
    const topComponents = [back()];
    return (react_1.default.createElement(index_1.DetailsPage, { title: "Appointment Details", endpoint: endpoint, fields: fields, topComponents: topComponents, otherComponentsToRender: canEdit ? [{
                title: 'Actions', content: editButton()
            }] : [] }));
};
exports.default = AppointmentDetailsPage;
