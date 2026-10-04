"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importDefault(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const SearchBar = ({ searchTerm, onSearchChange, searchFields, selectedFields, onToggleField, onToggleSelectAll, showFieldSelector, setShowFieldSelector, isLoading, dateFieldValue, onDateFieldChange, onResetFilters, setCurrentPage }) => {
    return (react_1.default.createElement(react_1.default.Fragment, null,
        react_1.default.createElement("div", { className: "d-flex justify-content-between align-items-center p-3 bg-light rounded mb-3" },
            react_1.default.createElement("div", { className: "position-relative w-50" },
                react_1.default.createElement("div", { className: "input-group" },
                    react_1.default.createElement("input", { type: "text", className: "form-control", placeholder: "Search...", value: searchTerm, onChange: (e) => onSearchChange(e.target.value) }),
                    searchFields.length > 0 && (react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-secondary", onClick: () => setShowFieldSelector(!showFieldSelector) },
                        react_1.default.createElement("i", { className: `bi bi-${showFieldSelector ? 'chevron-up' : 'chevron-down'}` }),
                        " Filter by"))),
                isLoading && (react_1.default.createElement("div", { className: "position-absolute top-50 end-0 translate-middle-y me-2" },
                    react_1.default.createElement("div", { className: "spinner-border spinner-border-sm text-secondary", role: "status" },
                        react_1.default.createElement("span", { className: "visually-hidden" }, "Loading..."))))),
            react_1.default.createElement(react_bootstrap_1.Form.Group, { controlId: "createdAt", className: "ms-3" },
                react_1.default.createElement(react_bootstrap_1.Form.Label, null, "Filter by date"),
                react_1.default.createElement(react_bootstrap_1.Form.Control, { type: "date", value: dateFieldValue, onChange: (e) => {
                        onDateFieldChange(e.target.value);
                        setCurrentPage(0);
                    } })),
            react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-danger", onClick: onResetFilters },
                react_1.default.createElement("i", { className: "bi bi-x-circle me-1" }),
                " Reset Filters")),
        showFieldSelector && searchFields.length > 0 && (react_1.default.createElement("div", { className: "bg-white p-3 mb-3 border rounded shadow-sm" },
            react_1.default.createElement("div", { className: "mb-2" },
                react_1.default.createElement(react_bootstrap_1.Form.Check, { type: "checkbox", id: "select-all-fields", label: "Select All", checked: selectedFields.length === searchFields.length, onChange: onToggleSelectAll })),
            react_1.default.createElement("div", { className: "d-flex flex-wrap gap-3" }, searchFields.map(field => (react_1.default.createElement(react_bootstrap_1.Form.Check, { key: field.name, type: "checkbox", id: `field-${field.name}`, label: field.label, checked: selectedFields.includes(field), onChange: () => onToggleField(field) }))))))));
};
exports.default = SearchBar;
