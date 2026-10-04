"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importDefault(require("react"));
const react_bootstrap_1 = require("react-bootstrap");
const dateFormatter_1 = __importDefault(require("../dateFormatter"));
const DoctorReportList = ({ reports, title }) => {
    const sharedStyle = `
        <style>
            body {
                font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                margin: 20px;
                color: #212529;
                background-color: #fff;
            }
            h1, h2, h3 {
                color: #0d6efd;
            }
            .report {
                margin-bottom: 40px;
                border: 1px solid #dee2e6;
                padding: 20px;
                border-radius: 5px;
                page-break-after: always;
            }
            .report-header {
                margin-bottom: 20px;
                padding-bottom: 15px;
                border-bottom: 2px solid #adb5bd;
            }
            .report-item {
                margin-bottom: 20px;
                padding: 10px 15px;
                border-left: 4px solid #0d6efd;
                background-color: #f8f9fa;
                border-radius: 3px;
            }
            .report-item-header {
                font-weight: bold;
                color: #0d6efd;
                margin-bottom: 5px;
            }
            .comment {
                margin-top: 10px;
                color: #6c757d;
                font-style: italic;
            }
            .print-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 30px;
                border-bottom: 1px solid #dee2e6;
                padding-bottom: 10px;
            }
            @page {
                size: auto;
                margin: 10mm;
            }
            @media print {
                body { margin: 0; padding: 0; }
                .print-header {
                    position: fixed;
                    top: 0;
                    width: 100%;
                    background-color: white;
                    padding: 10px;
                    border-bottom: 1px solid #dee2e6;
                }
            }
        </style>
    `;
    const printAllReports = () => {
        var _a;
        const html = `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <title>Medical Reports - ${((_a = reports[0]) === null || _a === void 0 ? void 0 : _a.patientName) || 'Reports'}</title>
                    ${sharedStyle}
                </head>
                <body>
                    <div class="print-header">
                        <div>
                            <h1>${title ? title : 'AfyaQuik Medical Reports'}</h1>
                            <p><strong>Printed:</strong> ${new Date().toLocaleString()}</p>
                        </div>
                    </div>
                    <br/><br/><br/>
                    ${reports.map(report => `
                        <div class="report">
                            <div class="report-header">
                                <h2>Report #${report.id}</h2>
                                <p><strong>Patient:</strong> ${report.patientName}</p>
                                <p><strong>Doctor:</strong> ${report.doctorName}</p>
                                <p><strong>Station:</strong> ${report.station}</p>
                                <p><strong>Created:</strong> ${(0, dateFormatter_1.default)(report.createdAt)}</p>
                            </div>

                            ${report.observationReportItems && report.observationReportItems.length > 0 ? `
                                <h3>Observation Reports</h3>
                                ${report.observationReportItems.map(item => `
                                    <div class="report-item">
                                        <div class="report-item-header">${item.itemName}</div>
                                        <div>${item.value}</div>
                                        ${item.comment ? `<div class="comment"><strong>Comment:</strong> ${item.comment}</div>` : ''}
                                    </div>
                                `).join('')}
                            ` : ''}

                            ${report.treatmentPlanReportItems && report.treatmentPlanReportItems.length > 0 ? `
                                <h3>Treatment Plans</h3>
                                ${report.treatmentPlanReportItems.map(item => `
                                    <div class="report-item">
                                        <div class="report-item-header">${item.treatmentPlanItemName}</div>
                                       
                                        <div dangerouslySetInnerHTML={{ __html: item.reportDetails || '' }} />
                                    </div>
                                `).join('')}
                            ` : ''} 

                            ${(!report.observationReportItems || report.observationReportItems.length === 0) &&
            (!report.treatmentPlanReportItems || report.treatmentPlanReportItems.length === 0) ?
            '<p class="text-muted">No items in this report.</p>' : ''}
                        </div>
                    `).join('')}
                </body>
            </html>
        `;
        const win = window.open('', '_blank');
        if (win) {
            win.document.write(html);
            win.document.close();
            win.onload = () => setTimeout(() => win.print(), 500);
        }
    };
    const printSingleReport = (report) => {
        const html = `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <title>Medical Report #${report.id}</title>
                    ${sharedStyle}
                </head>
                <body>
                    <div class="print-header">
                        <div>
                            <h1>${title ? title : 'AfyaQuik Medical Report'}</h1>
                            <p><strong>Printed:</strong> ${new Date().toLocaleString()}</p>
                        </div>
                    </div>
                    <br/><br/><br/>
                    <div class="report">
                        <div class="report-header">
                            <h2>Report #${report.id}</h2>
                            <p><strong>Patient:</strong> ${report.patientName}</p>
                            <p><strong>Doctor:</strong> ${report.doctorName}</p>
                            <p><strong>Station:</strong> ${report.station}</p>
                            <p><strong>Created:</strong> ${(0, dateFormatter_1.default)(report.createdAt)}</p>
                        </div>

                        ${report.observationReportItems && report.observationReportItems.length > 0 ? `
                            <h3>Observation Reports</h3>
                            ${report.observationReportItems.map(item => `
                                <div class="report-item">
                                    <div class="report-item-header">${item.itemName}</div>
                                    <div>${item.value}</div>
                                    ${item.comment ? `<div class="comment"><strong>Comment:</strong> ${item.comment}</div>` : ''}
                                </div>
                            `).join('')}
                        ` : ''}

                        ${report.treatmentPlanReportItems && report.treatmentPlanReportItems.length > 0 ? `
                            <h3>Treatment Plans</h3>
                            ${report.treatmentPlanReportItems.map(item => `
                                <div class="report-item">
                                    <div class="report-item-header">${item.treatmentPlanItemName}</div>
                                    <div dangerouslySetInnerHTML={{ __html: item.reportDetails || '' }} />
                                </div>
                            `).join('')}
                        ` : ''}

                        ${(!report.observationReportItems || report.observationReportItems.length === 0) &&
            (!report.treatmentPlanReportItems || report.treatmentPlanReportItems.length === 0) ?
            '<p class="text-muted">No items in this report.</p>' : ''}
                    </div>
                </body>
            </html>
        `;
        const win = window.open('', '_blank');
        if (win) {
            win.document.write(html);
            win.document.close();
            win.onload = () => setTimeout(() => win.print(), 500);
        }
    };
    return (react_1.default.createElement("div", null,
        react_1.default.createElement("div", { className: "d-flex justify-content-end mb-3 no-print" },
            react_1.default.createElement(react_bootstrap_1.ButtonGroup, null,
                react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-primary", onClick: printAllReports },
                    react_1.default.createElement("i", { className: "bi bi-printer me-1" }),
                    " Print All Reports"))),
        react_1.default.createElement(react_bootstrap_1.Accordion, { defaultActiveKey: "0", className: "my-4" }, reports.map((report, index) => {
            var _a, _b, _c, _d;
            return (react_1.default.createElement(react_bootstrap_1.Accordion.Item, { eventKey: String(index), key: report.id },
                react_1.default.createElement(react_bootstrap_1.Accordion.Header, null,
                    react_1.default.createElement("div", { className: "w-100 d-flex justify-content-between" },
                        react_1.default.createElement("div", null,
                            react_1.default.createElement("strong", null, "Patient:"),
                            " ",
                            report.patientName,
                            " ",
                            react_1.default.createElement("br", null),
                            react_1.default.createElement("strong", null, "Doctor:"),
                            " ",
                            report.doctorName,
                            " ",
                            react_1.default.createElement("br", null),
                            react_1.default.createElement("strong", null, "Station:"),
                            " ",
                            report.station),
                        react_1.default.createElement("div", { className: "text-muted small" },
                            react_1.default.createElement(react_bootstrap_1.Badge, { bg: "secondary", className: "me-2" },
                                "Report #",
                                report.id),
                            react_1.default.createElement("div", null,
                                "Created: ",
                                (0, dateFormatter_1.default)(report.createdAt))))),
                react_1.default.createElement(react_bootstrap_1.Accordion.Body, null,
                    react_1.default.createElement("div", { className: "d-flex justify-content-end mb-3 no-print" },
                        react_1.default.createElement(react_bootstrap_1.Button, { variant: "outline-secondary", size: "sm", onClick: () => printSingleReport(report) },
                            react_1.default.createElement("i", { className: "bi bi-printer me-1" }),
                            " Print This Report")), (_a = report.observationReportItems) === null || _a === void 0 ? void 0 :
                    _a.map(item => (react_1.default.createElement(react_bootstrap_1.Card, { key: item.id, className: "mb-3 border-0 shadow-sm" },
                        react_1.default.createElement(react_bootstrap_1.Card.Header, { className: "bg-light fw-semibold" }, item.itemName),
                        react_1.default.createElement(react_bootstrap_1.Card.Body, null,
                            react_1.default.createElement("div", { dangerouslySetInnerHTML: { __html: item.value } }),
                            item.comment && (react_1.default.createElement("div", { className: "text-muted mt-2" },
                                react_1.default.createElement("strong", null, "Comment:"),
                                " ",
                                item.comment)))))), (_b = report.treatmentPlanReportItems) === null || _b === void 0 ? void 0 :
                    _b.map(item => (react_1.default.createElement(react_bootstrap_1.Card, { key: item.id, className: "mb-3 border-0 shadow-sm" },
                        react_1.default.createElement(react_bootstrap_1.Card.Header, { className: "bg-light fw-semibold" }, item.treatmentPlanItemName),
                        react_1.default.createElement(react_bootstrap_1.Card.Body, null,
                            react_1.default.createElement("div", null, item.reportDetails))))),
                    !((_c = report.observationReportItems) === null || _c === void 0 ? void 0 : _c.length) && !((_d = report.treatmentPlanReportItems) === null || _d === void 0 ? void 0 : _d.length) && (react_1.default.createElement("p", { className: "text-muted" }, "No items in this report.")))));
        }))));
};
exports.default = DoctorReportList;
