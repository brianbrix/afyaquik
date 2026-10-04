"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = require("react");
const api_1 = __importDefault(require("../api"));
const react_2 = __importDefault(require("react"));
const DataTable_1 = __importDefault(require("../DataTable"));
const ToastContext_1 = require("../ToastContext");
const columns = [
    { header: 'First Name', accessor: 'firstName' },
    { header: 'Last Name', accessor: 'lastName' },
    { header: 'When Registered', accessor: 'createdAt', type: 'datetime' },
    { header: 'Phone', accessor: 'contactInfo.phoneNumber' }
];
const PatientList = () => {
    const { showToast } = (0, ToastContext_1.useToast)();
    const [patients, setPatients] = (0, react_1.useState)([]);
    (0, react_1.useEffect)(() => {
        (0, api_1.default)("/search", { method: 'POST', body: {} })
            .then(data => {
            setPatients(data);
        })
            .catch(err => console.error(err));
        showToast('Search for patients or add a new patient to create appointment or add visit', 'warning');
    }, []);
    const searchFields = [
        {
            name: "firstName",
            label: "First Name",
        },
        {
            name: "lastName",
            label: "Last Name",
        },
        {
            name: "secondName",
            label: "Second Name",
        },
        {
            name: "nationalId",
            label: "National ID",
        }
    ];
    return (react_2.default.createElement(DataTable_1.default, { title: "Patient List", columns: columns, data: patients, editView: "index.html#/patients/#id/edit", addView: "index.html#/patients/add", detailsView: "index.html#/patients/#id/details", searchFields: searchFields, searchEntity: 'patients', requestMethod: 'POST', isSearchable: true, dataEndpoint: '/search' }));
};
exports.default = PatientList;
