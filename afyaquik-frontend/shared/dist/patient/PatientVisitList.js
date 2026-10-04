"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = require("react");
const api_1 = __importDefault(require("../api"));
const react_2 = __importDefault(require("react"));
const DataTable_1 = __importDefault(require("../DataTable"));
const columns = [
    { header: 'Visit Type', accessor: 'visitType' },
    { header: 'Date of Visit', accessor: 'visitDate', type: 'date' }
];
const PatientVisitList = ({ patientId }) => {
    // let  params = useParams();
    // const id = Number(params.id);
    const [patientVisits, setPatientVisits] = (0, react_1.useState)([]);
    (0, react_1.useEffect)(() => {
        (0, api_1.default)(`/patients/${patientId}/visits`, { method: 'GET' })
            .then(data => {
            setPatientVisits(data);
        })
            .catch(err => console.error(err));
    }, []);
    const patientName = localStorage.getItem('patientName');
    const searchFields = [
        {
            name: 'createdAt',
            label: 'When added',
        },
        {
            name: 'visitType',
            label: 'Visit Type',
        },
        {
            name: 'visitStatus',
            label: 'Visit Status',
        },
        {
            name: 'visitDate',
            label: 'Visit Date',
        }
    ];
    return (react_2.default.createElement(DataTable_1.default, { title: `Visits by ${patientName}`, columns: columns, data: patientVisits, editView: "index.html#/visits/#id/edit", addView: `index.html#/patients/${patientId}/visits/add`, detailsView: "index.html#/visits/#id/details", searchFields: searchFields, 
        // searchEntity={'visits'}
        requestMethod: 'GET', dataEndpoint: `/patients/${patientId}/visits` }));
};
exports.default = PatientVisitList;
