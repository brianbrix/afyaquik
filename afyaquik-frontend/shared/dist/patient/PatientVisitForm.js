"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.patientName = void 0;
const StepForm_1 = __importDefault(require("../StepForm"));
const react_1 = __importDefault(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const patientName = function (id) {
    const patientName = localStorage.getItem('patientName');
    if (!patientName)
        window.location.href = `index.html#/patient/${id}/details`;
    return (react_1.default.createElement(react_bootstrap_1.Card, { className: "shadow-sm" },
        react_1.default.createElement(react_bootstrap_1.Card.Header, { className: "bg-primary text-white" },
            react_1.default.createElement("h5", { className: "mb-0" }, patientName))));
};
exports.patientName = patientName;
const PatientVisitForm = ({ formConfig, onSubmit, idFromParent, defaultValues, submitButtonLabel }) => {
    // let  params = useParams();
    // const id = Number(params.id);
    console.log(' Id', idFromParent);
    return (react_1.default.createElement(react_1.default.Fragment, null,
        react_1.default.createElement(StepForm_1.default, { config: formConfig, onSubmit: onSubmit, idFromParent: idFromParent, defaultValues: defaultValues, submitButtonLabel: submitButtonLabel })));
};
exports.default = PatientVisitForm;
