"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = require("react");
const api_1 = __importDefault(require("../api"));
const react_2 = __importDefault(require("react"));
const DataTable_1 = __importDefault(require("../DataTable"));
const AssignmentsList = ({ visitId, columns, dataEndpoint, editView, addView, detailsView, title, userId, editTitle, editClassName, editButtonAction, whichOfficer = 'attending' }) => {
    const [plans, setPlans] = (0, react_1.useState)([]);
    let url = `/patient/visits/${visitId}/assignments`;
    console.log("AssignmentsList", visitId, userId, whichOfficer);
    if (userId) {
        url = `/patient/visits/${visitId}/assignments/${userId}`;
    }
    (0, react_1.useEffect)(() => {
        if (userId)
            url += `?whichOfficer=${whichOfficer}`;
        (0, api_1.default)(dataEndpoint ? dataEndpoint : url, { method: 'GET' })
            .then(data => {
            setPlans(data);
        })
            .catch(err => console.error(err));
    }, []);
    const dataTableProps = {
        title: title ? `${title}` : "Assignment",
        columns,
        data: plans,
        isSearchable: false,
        requestMethod: 'GET',
        dataEndpoint: url
    };
    if (userId) {
        dataTableProps.additionalParams = { whichOfficer: whichOfficer };
    }
    if (addView) {
        dataTableProps.addView = `index.html#/visits/${visitId}/assign`;
        dataTableProps.addTitle = 'Add Assignment';
    }
    if (editView) {
        dataTableProps.editView = editView;
        dataTableProps.editTitle = 'Add Observation';
        dataTableProps.editClassName = 'bi bi-plus-circle me-1';
    }
    if (editClassName) {
        dataTableProps.editClassName = editClassName;
    }
    if (editTitle) {
        dataTableProps.editTitle = editTitle;
    }
    if (editButtonAction) {
        dataTableProps.editButtonAction = editButtonAction;
    }
    if (detailsView) {
        dataTableProps.detailsView = detailsView;
        dataTableProps.detailsTitle = 'Add Treatment Plan';
        dataTableProps.detailsClassName = 'bi bi-plus-square me-1';
    }
    return react_2.default.createElement(DataTable_1.default, Object.assign({}, dataTableProps));
};
exports.default = AssignmentsList;
