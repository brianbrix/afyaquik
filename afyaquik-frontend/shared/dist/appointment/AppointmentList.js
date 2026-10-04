"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = require("react");
const react_2 = __importDefault(require("react"));
const DataTable_1 = __importDefault(require("../DataTable"));
const columns = [
    { header: 'First Name', accessor: 'patient.firstName' },
    { header: 'Last Name', accessor: 'patient.lastName' },
    { header: 'Doctor Name', accessor: 'doctor.username' },
    { header: 'Date scheduled', accessor: 'appointmentDateTime', type: 'datetime' },
    { header: 'Status', accessor: 'status' }
];
const AppointmentList = ({ patientId, data, title, query, canEdit = true }) => {
    const [appointments, setAppointments] = (0, react_1.useState)([]);
    const searchFields = [
        {
            name: "patient.firstName",
            label: "First Name",
        },
        {
            name: "patient.lastName",
            label: "Last Name",
        },
        {
            name: "patient.secondName",
            label: "Second Name",
        },
        {
            name: "doctor.username",
            label: "Doctor",
        }
    ];
    return (data ? (react_2.default.createElement(DataTable_1.default, { title: title ? title : "Appointments List", columns: columns, data: data, searchEntity: 'appointments' })) :
        (query ? (react_2.default.createElement(DataTable_1.default, { title: title ? title : "Appointments List", columns: columns, data: appointments, editView: canEdit ? "index.html#/appointments/#id/edit" : undefined, showMultipleDeleteButton: canEdit, detailsView: "index.html#/appointments/#id/details", searchFields: searchFields, searchEntity: 'appointments', requestMethod: 'POST', isSearchable: true, dateFieldName: 'appointmentDateTime', dataEndpoint: '/search', combinedSearchFieldsAndTerms: query })) :
            (react_2.default.createElement(DataTable_1.default, { title: title ? title : "Appointments List", columns: columns, data: appointments, editView: canEdit ? "index.html#/appointments/#id/edit" : undefined, addView: canEdit ? "index.html#/patients" : undefined, showMultipleDeleteButton: canEdit, detailsView: "index.html#/appointments/#id/details", searchFields: searchFields, searchEntity: 'appointments', requestMethod: 'POST', isSearchable: true, dateFieldName: 'appointmentDateTime', dataEndpoint: '/search' }))));
};
exports.default = AppointmentList;
