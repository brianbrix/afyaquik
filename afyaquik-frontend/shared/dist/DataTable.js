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
const react_1 = __importStar(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const papaparse_1 = __importDefault(require("papaparse"));
const api_1 = __importDefault(require("./api"));
const index_1 = require("./index");
const dateFormatter_1 = __importDefault(require("./dateFormatter"));
const formaterUtils_1 = require("./formaterUtils");
function DataTable({ loading = false, error, title, onRef, columns, data: initialData = [], editView, editTitle = '', editClassName = 'bi bi-pencil', deleteButtonEnabled = true, deleteClassName = 'bi bi-trash', deleteButtonAction, deleteTitle = '', addView, addTitle = 'Add Record', addClassName = 'bi bi-plus-circle me-1', detailsView, detailsTitle = 'Details', detailsClassName = 'bi bi-eye', dataEndpoint, requestMethod, searchFields = [], searchEntity = 'patients', defaultPageSize = 10, editButtonAction, detailsButtonAction, additionalParams, editButtonEnabled = true, detailsButtonEnabled = true, showDeletedRecords = false, isSearchable, dateFieldName = 'createdAt', combinedSearchFieldsAndTerms, showSelectionMode = false, selectionModeAction, selectionModeActionTitle = 'Process Selected', selectionModeActionDisabled = (selectedItems) => selectedItems.length === 0, showPagination = true, deleteMultipleButtonTitle, deleteEndpoint, showMultipleDeleteButton, preventDeleteMultipleAction }) {
    let [searchTerm, setSearchTerm] = (0, react_1.useState)('');
    const [sortField, setSortField] = (0, react_1.useState)(null);
    const [sortDirection, setSortDirection] = (0, react_1.useState)('asc');
    const [currentPage, setCurrentPage] = (0, react_1.useState)(0);
    const [pageSize, setPageSize] = (0, react_1.useState)(defaultPageSize);
    const [data, setData] = (0, react_1.useState)(initialData);
    const [totalElements, setTotalElements] = (0, react_1.useState)(0);
    const [isSearching, setIsSearching] = (0, react_1.useState)(false);
    const [loadError, setLoadError] = (0, react_1.useState)('');
    const [selectedFields, setSelectedFields] = (0, react_1.useState)(searchFields);
    const [showFieldSelector, setShowFieldSelector] = (0, react_1.useState)(false);
    const [dateFieldValue, setDateFieldValue] = (0, react_1.useState)('');
    const [selectedRows, setSelectedRows] = (0, react_1.useState)([]);
    const [selectAll, setSelectAll] = (0, react_1.useState)(false);
    const onResetFilters = () => {
        setSearchTerm('');
        setDateFieldValue('');
        setSelectedFields([]);
        setCurrentPage(0);
    };
    // Selection mode handlers
    const handleSelectRow = (row, isSelected) => {
        if (isSelected) {
            setSelectedRows([...selectedRows, row]);
        }
        else {
            setSelectedRows(selectedRows.filter(r => r.id !== row.id));
        }
    };
    (0, react_1.useEffect)(() => {
        if (onRef) {
            onRef({
                refreshData: () => {
                    fetchData(currentPage, pageSize, sortField ? `${sortField},${sortDirection}` : undefined);
                },
                getSelectedRows: () => selectedRows
            });
        }
    }, [currentPage, pageSize, sortField, sortDirection, selectedRows]);
    const handleDeleteMultiple = () => __awaiter(this, void 0, void 0, function* () {
        if (selectedRows.length === 0) {
            return; // No records selected
        }
        if (typeof preventDeleteMultipleAction === 'function') {
            if (preventDeleteMultipleAction(selectedRows)) {
                return;
            }
        }
        const confirmDelete = window.confirm(`Are you sure you want to delete ${selectedRows.length} records?`);
        if (!confirmDelete) {
            return;
        }
        try {
            if (!deleteEndpoint) { //use default global delete endpoint
                yield (0, api_1.default)('/delete', {
                    method: 'POST',
                    body: {
                        ids: selectedRows.map(row => row.id),
                        entityName: searchEntity
                    },
                });
            }
            // Refresh data after deletion
            fetchData(currentPage, pageSize, sortField ? `${sortField},${sortDirection}` : undefined);
            setSelectedRows([]); // Clear selected rows after deletion
        }
        catch (error) {
            console.error('Delete error:', error);
        }
    });
    const handleSelectAll = (isSelected) => {
        setSelectAll(isSelected);
        if (isSelected) {
            setSelectedRows([...data]);
        }
        else {
            setSelectedRows([]);
        }
    };
    // Check if a row is selected
    const isRowSelected = (row) => {
        return selectedRows.some(r => r.id === row.id);
    };
    const fetchData = (page, size, sort) => __awaiter(this, void 0, void 0, function* () {
        var _a, _b, _c, _d, _e, _f;
        setIsSearching(true);
        setLoadError('');
        const query = [searchTerm, showDeletedRecords ? '' : 'deleted=false', combinedSearchFieldsAndTerms].filter(Boolean).join(',');
        try {
            let params = Object.assign(Object.assign(Object.assign(Object.assign({ page,
                size }, (sort && { sort })), ((dateFieldValue && dateFieldName) && { dateFilter: dateFieldName ? dateFieldName + '#' + dateFieldValue : 'createdAt' + '#' + dateFieldValue })), (searchEntity && { searchEntity: searchEntity })), (additionalParams));
            let response;
            if (requestMethod === 'GET') {
                const queryParams = new URLSearchParams();
                Object.entries(params).forEach(([key, value]) => {
                    queryParams.append(key, String(value));
                });
                if (query) {
                    queryParams.append('query', query);
                    selectedFields.forEach(field => queryParams.append('fields', field.name));
                }
                response = yield (0, api_1.default)(`${dataEndpoint}?${queryParams.toString()}`);
            }
            else {
                const requestBody = Object.assign(Object.assign(Object.assign({}, params), { searchEntity: searchEntity }), (query && {
                    query,
                    searchFields: selectedFields.map(f => f.name)
                }));
                response = yield (0, api_1.default)(dataEndpoint + '', {
                    method: 'POST',
                    body: requestBody,
                });
            }
            const results = (_a = response.results) !== null && _a !== void 0 ? _a : response;
            const pageInfo = (_b = results.page) !== null && _b !== void 0 ? _b : results;
            const rows = Array.isArray(results) ? results : (_c = results.content) !== null && _c !== void 0 ? _c : [];
            setData(rows);
            setTotalElements((_d = pageInfo.totalElements) !== null && _d !== void 0 ? _d : rows.length);
            setCurrentPage((_e = pageInfo.number) !== null && _e !== void 0 ? _e : page);
            setPageSize((_f = pageInfo.size) !== null && _f !== void 0 ? _f : size);
        }
        catch (error) {
            setLoadError(error instanceof Error ? error.message : 'Unable to load records.');
        }
        finally {
            setIsSearching(false);
        }
    });
    (0, react_1.useEffect)(() => {
        if (dataEndpoint && (searchTerm.length >= 3 || searchTerm.length === 0)) {
            let sortParam = sortField ? `${sortField},${sortDirection}` : 'createdAt,desc';
            if (dateFieldName && !sortField) {
                sortParam = `${dateFieldName},desc`;
            }
            fetchData(currentPage, pageSize, sortParam);
        }
    }, [currentPage, pageSize, sortField, sortDirection, searchTerm, selectedFields, dateFieldValue, combinedSearchFieldsAndTerms, dataEndpoint]);
    const handleSort = (field) => {
        if (sortField === field) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
        }
        else {
            setSortField(field);
            setSortDirection('asc');
        }
        setCurrentPage(0); // Reset to first page when sorting changes
    };
    const handleFieldToggle = (field) => {
        const newFields = selectedFields.includes(field)
            ? selectedFields.filter(f => f !== field)
            : [...selectedFields, field];
        setSelectedFields(newFields);
        setCurrentPage(0); // Reset to first page when search fields change
    };
    const toggleSelectAllFields = () => {
        setSelectedFields(selectedFields.length === searchFields.length ? [] : [...searchFields]);
        setCurrentPage(0);
    };
    /**
     * Resolves a value from an object using a dot-notation path
     * @param obj - The object to extract value from
     * @param path - Dot-notation path (e.g., 'user.address.city')
     * @param fallback - Value to return if path doesn't exist
     * @returns The resolved value or fallback
     */
    function resolveValue(obj, path, fallback = 'N/A') {
        return path.split('.').reduce((acc, part) => (acc && acc[part] !== undefined) ? acc[part] : fallback, obj);
    }
    /**
     * Formats a cell value based on its type
     * @param value - The raw value to format
     * @param type - The type of formatting to apply
     * @returns The formatted value
     */
    function formatCellValue(value, type) {
        if (value === undefined || value === null) {
            return 'N/A';
        }
        switch (type) {
            case 'date':
                return (0, dateFormatter_1.default)(value, true);
            case 'datetime':
                return (0, dateFormatter_1.default)(value);
            case 'boolean':
                return value ? 'Yes' : 'No';
            case 'currency':
                return (0, formaterUtils_1.formatCurrency)(value);
            case 'number':
                return (0, formaterUtils_1.formatNumber)(value);
            case 'percentage':
                return (0, formaterUtils_1.formatPercentage)(value);
            case 'wysiwyg':
                return (0, formaterUtils_1.formatWysiwyg)(value);
            default:
                return value;
        }
    }
    const downloadCSV = () => {
        const csvData = (data).map(record => {
            const row = {};
            columns.forEach(col => {
                row[col.header] = resolveValue(record, col.accessor);
            });
            return row;
        });
        const csv = papaparse_1.default.unparse(csvData);
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${title.replace(/\s+/g, '_').toLowerCase()}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };
    if (loading)
        return react_1.default.createElement("div", null, "Loading...");
    if (error)
        return react_1.default.createElement("div", null,
            "Error: ",
            error);
    return (react_1.default.createElement("div", { className: "container my-4" },
        loadError && react_1.default.createElement("div", { className: "alert alert-danger", role: "alert" },
            loadError,
            react_1.default.createElement("button", { className: "btn btn-sm btn-outline-danger ms-3", onClick: () => fetchData(currentPage, pageSize) },
                react_1.default.createElement("i", { className: "bi bi-arrow-clockwise me-1" }),
                "Retry")),
        react_1.default.createElement("div", { className: "d-flex justify-content-between align-items-center mb-3" },
            react_1.default.createElement("h5", { className: "text-primary fw-semibold m-0" }, title),
            react_1.default.createElement("div", null,
                showSelectionMode && selectionModeAction && (react_1.default.createElement(react_bootstrap_1.Button, { variant: "success", className: "me-2", onClick: () => selectionModeAction(selectedRows), disabled: selectionModeActionDisabled(selectedRows) }, selectionModeActionTitle)),
                react_1.default.createElement(react_bootstrap_1.Button, { variant: "success", className: "me-2", onClick: downloadCSV },
                    react_1.default.createElement("i", { className: "bi bi-download me-1" }),
                    " Download CSV"),
                addView && (react_1.default.createElement(react_bootstrap_1.Button, { variant: "primary", className: "me-2", onClick: () => window.location.href = addView },
                    react_1.default.createElement("i", { className: addClassName }),
                    " ",
                    addTitle)),
                showMultipleDeleteButton && (react_1.default.createElement(react_bootstrap_1.Button, { variant: "danger", className: "me-2", onClick: handleDeleteMultiple, disabled: selectedRows.length === 0 }, deleteMultipleButtonTitle || 'Delete Selected')))),
        showSelectionMode && (react_1.default.createElement("div", { className: "mb-3" },
            react_1.default.createElement(react_bootstrap_1.Form.Check, { type: "checkbox", label: "Select All", checked: selectAll, onChange: (e) => handleSelectAll(e.target.checked) }))),
        isSearchable && (react_1.default.createElement(index_1.SearchBar, { searchTerm: searchTerm, onSearchChange: setSearchTerm, searchFields: searchFields, selectedFields: selectedFields, onToggleField: handleFieldToggle, onToggleSelectAll: toggleSelectAllFields, showFieldSelector: showFieldSelector, setShowFieldSelector: setShowFieldSelector, isLoading: isSearching, dateFieldValue: dateFieldValue, onDateFieldChange: setDateFieldValue, onResetFilters: onResetFilters, setCurrentPage: setCurrentPage })),
        react_1.default.createElement(react_bootstrap_1.Table, { bordered: true, hover: true, responsive: true, className: "table-sm align-middle" },
            react_1.default.createElement("thead", { className: "table-light" },
                react_1.default.createElement("tr", null,
                    react_1.default.createElement("th", { className: "py-3 ps-4" }, "#"),
                    showSelectionMode && (react_1.default.createElement("th", { className: "py-3 ps-4", style: { width: '50px' } }, "Select")),
                    columns.map(col => (react_1.default.createElement("th", { key: col.accessor, className: "py-3 ps-4", style: { cursor: (col.sortable === true || col.sortable === undefined) ? 'pointer' : 'default' }, onClick: () => (col.sortable === true || col.sortable === undefined) && handleSort(col.accessor) },
                        col.header,
                        sortField === col.accessor && (isSearching ? ' ⏳' : (sortDirection === 'asc' ? ' 🔼' : ' 🔽'))))),
                    (editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) && react_1.default.createElement("th", null, "Actions"))),
            react_1.default.createElement("tbody", null, data.length === 0 ? (react_1.default.createElement("tr", null,
                react_1.default.createElement("td", { colSpan: columns.length + (showSelectionMode ? 1 : 0) + ((editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) ? 1 : 0), className: "text-center" }, "No records found"))) : (data.map((record, index) => (react_1.default.createElement("tr", { key: record.id },
                react_1.default.createElement("td", null, (currentPage * pageSize) + index + 1),
                showSelectionMode && (react_1.default.createElement("td", null,
                    react_1.default.createElement(react_bootstrap_1.Form.Check, { type: "checkbox", checked: isRowSelected(record), onChange: (e) => handleSelectRow(record, e.target.checked) }))),
                columns.map(col => {
                    const value = resolveValue(record, col.accessor);
                    const display = formatCellValue(value, col.type);
                    return (react_1.default.createElement("td", { key: `${record.id}-${col.accessor}` }, display));
                }),
                (editView || detailsView || (!showSelectionMode && (detailsButtonAction || editButtonAction))) && (react_1.default.createElement("td", null,
                    (detailsView || detailsButtonAction) && (react_1.default.createElement(react_bootstrap_1.Button, { disabled: typeof detailsButtonEnabled === 'function'
                            ? !detailsButtonEnabled(record)
                            : !detailsButtonEnabled, variant: "secondary", className: "me-2", onClick: () => {
                            if (detailsButtonAction) {
                                detailsButtonAction(record);
                            }
                            else if (detailsView) {
                                window.location.href = detailsView.replace("#id", String(record.id));
                            }
                        } },
                        react_1.default.createElement("i", { className: detailsClassName }),
                        " ",
                        detailsTitle)),
                    (editView || editButtonAction) && (react_1.default.createElement(react_bootstrap_1.Button, { disabled: typeof editButtonEnabled === 'function'
                            ? !editButtonEnabled(record)
                            : !editButtonEnabled, variant: "primary", onClick: () => {
                            if (editButtonAction) {
                                editButtonAction(record);
                            }
                            else if (editView) {
                                window.location.href = editView.replace("#id", String(record.id));
                            }
                        } },
                        react_1.default.createElement("i", { className: editClassName }),
                        " ",
                        editTitle)),
                    deleteButtonAction && (react_1.default.createElement(react_bootstrap_1.Button, { disabled: typeof deleteButtonEnabled === 'function'
                            ? !deleteButtonEnabled(record)
                            : !deleteButtonEnabled, variant: "danger", className: "ms-2", onClick: () => deleteButtonAction(record) },
                        react_1.default.createElement("i", { className: deleteClassName }),
                        " ",
                        deleteTitle)))))))))),
        showPagination && (react_1.default.createElement("div", { className: "d-flex justify-content-between align-items-center mt-3" },
            react_1.default.createElement("div", null,
                react_1.default.createElement("small", { className: "text-muted" },
                    "Showing ",
                    data.length,
                    " of ",
                    totalElements,
                    " records")),
            react_1.default.createElement("div", { className: "d-flex align-items-center" },
                react_1.default.createElement("select", { className: "form-select form-select-sm me-2", style: { width: 'auto' }, value: pageSize, onChange: (e) => {
                        setPageSize(Number(e.target.value));
                        setCurrentPage(0);
                    } }, [5, 10, 20, 50].map(size => (react_1.default.createElement("option", { key: size, value: size },
                    size,
                    " per page")))),
                react_1.default.createElement("nav", null,
                    react_1.default.createElement("ul", { className: "pagination pagination-sm mb-0" },
                        react_1.default.createElement("li", { className: `page-item ${currentPage === 0 ? 'disabled' : ''}` },
                            react_1.default.createElement("button", { className: "page-link", onClick: () => setCurrentPage(p => p - 1), disabled: currentPage === 0 }, "Prev")),
                        Array.from({ length: Math.min(5, Math.ceil(totalElements / pageSize)) }, (_, i) => {
                            // Show pages around current page
                            let pageNum = i;
                            if (currentPage >= 3 && currentPage < Math.ceil(totalElements / pageSize) - 3) {
                                pageNum = currentPage - 2 + i;
                            }
                            else if (currentPage >= Math.ceil(totalElements / pageSize) - 3) {
                                pageNum = Math.max(0, Math.ceil(totalElements / pageSize) - 5) + i;
                            }
                            return (react_1.default.createElement("li", { key: pageNum, className: `page-item ${pageNum === currentPage ? 'active' : ''}` },
                                react_1.default.createElement("button", { className: "page-link", onClick: () => setCurrentPage(pageNum), disabled: pageNum >= Math.ceil(totalElements / pageSize) }, pageNum + 1)));
                        }),
                        react_1.default.createElement("li", { className: `page-item ${currentPage >= Math.ceil(totalElements / pageSize) - 1 ? 'disabled' : ''}` },
                            react_1.default.createElement("button", { className: "page-link", onClick: () => setCurrentPage(p => p + 1), disabled: currentPage >= Math.ceil(totalElements / pageSize) - 1 }, "Next")))))))));
}
exports.default = DataTable;
