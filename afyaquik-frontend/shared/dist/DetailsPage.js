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
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importStar(require("react"));
const api_1 = __importDefault(require("./api"));
const react_bootstrap_1 = require("react-bootstrap"); // Add Tabs components
const dateFormatter_1 = __importDefault(require("./dateFormatter"));
const DetailsPage = ({ fields, endpoint, title, otherComponentsToRender, topComponents, activeTab }) => {
    const [record, setRecord] = (0, react_1.useState)(null);
    const [loading, setLoading] = (0, react_1.useState)(true);
    const [initialActiveTab, setInitialActiveTab] = (0, react_1.useState)(null);
    (0, react_1.useEffect)(() => {
        (0, api_1.default)(endpoint)
            .then(response => {
            if (endpoint.startsWith('/patients/')) {
                let patientName = response.firstName;
                if (response.lastName)
                    patientName += " " + response.lastName;
                localStorage.setItem("patientName", patientName);
            }
            setRecord(response);
        })
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, [endpoint]);
    (0, react_1.useEffect)(() => {
        if (activeTab && (otherComponentsToRender === null || otherComponentsToRender === void 0 ? void 0 : otherComponentsToRender.some(comp => comp.title === activeTab))) {
            setInitialActiveTab(activeTab);
        }
        else if (otherComponentsToRender === null || otherComponentsToRender === void 0 ? void 0 : otherComponentsToRender.length) {
            setInitialActiveTab(otherComponentsToRender[0].title);
        }
    }, [otherComponentsToRender, activeTab]);
    if (loading)
        return react_1.default.createElement("div", { className: "text-center py-5" },
            react_1.default.createElement(react_bootstrap_1.Spinner, { animation: "border" }));
    if (!record)
        return react_1.default.createElement("div", { className: "text-danger text-center py-5" }, "Record not found.");
    return (react_1.default.createElement("div", { className: "container py-5" },
        topComponents && topComponents.length > 0 && (react_1.default.createElement("div", { className: "mb-4" },
            react_1.default.createElement("div", { className: "d-flex flex-wrap gap-2 justify-content-start align-items-center" }, topComponents.map((component, idx) => (react_1.default.createElement("div", { key: idx }, component)))))),
        react_1.default.createElement(react_bootstrap_1.Card, { className: "shadow-sm" },
            react_1.default.createElement(react_bootstrap_1.Card.Header, { className: "bg-primary text-white" },
                react_1.default.createElement("h5", { className: "mb-0" }, title ? title : "Details")),
            react_1.default.createElement(react_bootstrap_1.Card.Body, null,
                react_1.default.createElement("div", { className: "row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3" }, fields.map(({ label, accessor, type }) => {
                    const value = resolveValue(record, accessor);
                    return (react_1.default.createElement("div", { key: accessor, className: "col" },
                        react_1.default.createElement("div", { className: "p-2 border rounded h-100" },
                            react_1.default.createElement("div", { className: "text-truncate", title: label },
                                react_1.default.createElement("strong", null,
                                    label,
                                    ":")),
                            type === 'wysiwyg' ? (react_1.default.createElement("div", { dangerouslySetInnerHTML: { __html: value || '' } })) : (react_1.default.createElement("div", { className: "text-truncate", title: value }, type === 'date' || type === 'datetime' ? (0, dateFormatter_1.default)(value) : value)))));
                })))),
        otherComponentsToRender && otherComponentsToRender.length > 0 && (react_1.default.createElement(react_bootstrap_1.Card, { className: "mt-4 shadow-sm" },
            react_1.default.createElement(react_bootstrap_1.Card.Body, null,
                react_1.default.createElement(react_bootstrap_1.Tabs, { activeKey: initialActiveTab || '', onSelect: (k) => setInitialActiveTab(k), className: "mb-3", id: "details-page-tabs" }, otherComponentsToRender.map(({ title, content }) => (react_1.default.createElement(react_bootstrap_1.Tab, { key: title, eventKey: title, title: title },
                    react_1.default.createElement("div", { className: "mt-3" }, content))))))))));
};
function resolveValue(obj, path) {
    var _a;
    return (_a = path.split('.').reduce((acc, key) => acc === null || acc === void 0 ? void 0 : acc[key], obj)) !== null && _a !== void 0 ? _a : 'N/A';
}
exports.default = DetailsPage;
