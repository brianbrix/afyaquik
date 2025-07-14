import React from 'react';
import { Accordion, Card, Badge, Button, ButtonGroup } from 'react-bootstrap';
import formatDate from "../dateFormatter";

interface ObservationReportItem {
    id: number;
    itemId: number;
    itemName: string;
    reportId: number;
    value: string;
    comment?: string | null;
}

interface TreatmentPlanReportItem {
    id: number;
    treatmentPlanId: number;
    reportDetails: string;
    treatmentPlanItemName: string;
}

interface DoctorReport {
    id: number;
    patientVisitId: number;
    patientName: string;
    station: string;
    doctorId: number;
    doctorName: string;
    createdAt: string;
    updatedAt: string;
    observationReportItems?: ObservationReportItem[];
    treatmentPlanReportItems?: TreatmentPlanReportItem[];
}

const DoctorReportList: React.FC<{ reports: DoctorReport[], title?: string }> = ({ reports, title }) => {
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
        const html = `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <title>Medical Reports - ${reports[0]?.patientName || 'Reports'}</title>
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
                                <p><strong>Created:</strong> ${formatDate(report.createdAt)}</p>
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
                                        <div>${item.reportDetails}</div>
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

    const printSingleReport = (report: DoctorReport) => {
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
                            <p><strong>Created:</strong> ${formatDate(report.createdAt)}</p>
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
                                    <div>${item.reportDetails}</div>
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

    return (
        <div>
            <div className="d-flex justify-content-end mb-3 no-print">
                <ButtonGroup>
                    <Button variant="outline-primary" onClick={printAllReports}>
                        <i className="bi bi-printer me-1"></i> Print All Reports
                    </Button>
                </ButtonGroup>
            </div>

            <Accordion defaultActiveKey="0" className="my-4">
                {reports.map((report, index) => (
                    <Accordion.Item eventKey={String(index)} key={report.id}>
                        <Accordion.Header>
                            <div className="w-100 d-flex justify-content-between">
                                <div>
                                    <strong>Patient:</strong> {report.patientName} <br />
                                    <strong>Doctor:</strong> {report.doctorName} <br />
                                    <strong>Station:</strong> {report.station}
                                </div>
                                <div className="text-muted small">
                                    <Badge bg="secondary" className="me-2">
                                        Report #{report.id}
                                    </Badge>
                                    <div>Created: {formatDate(report.createdAt)}</div>
                                </div>
                            </div>
                        </Accordion.Header>
                        <Accordion.Body>
                            <div className="d-flex justify-content-end mb-3 no-print">
                                <Button variant="outline-secondary" size="sm" onClick={() => printSingleReport(report)}>
                                    <i className="bi bi-printer me-1"></i> Print This Report
                                </Button>
                            </div>

                            {report.observationReportItems?.map(item => (
                                <Card key={item.id} className="mb-3 border-0 shadow-sm">
                                    <Card.Header className="bg-light fw-semibold">{item.itemName}</Card.Header>
                                    <Card.Body>
                                        <div dangerouslySetInnerHTML={{ __html: item.value }} />
                                        {item.comment && (
                                            <div className="text-muted mt-2"><strong>Comment:</strong> {item.comment}</div>
                                        )}
                                    </Card.Body>
                                </Card>
                            ))}

                            {report.treatmentPlanReportItems?.map(item => (
                                <Card key={item.id} className="mb-3 border-0 shadow-sm">
                                    <Card.Header className="bg-light fw-semibold">{item.treatmentPlanItemName}</Card.Header>
                                    <Card.Body>
                                        <div>{item.reportDetails}</div>
                                    </Card.Body>
                                </Card>
                            ))}

                            {!report.observationReportItems?.length && !report.treatmentPlanReportItems?.length && (
                                <p className="text-muted">No items in this report.</p>
                            )}
                        </Accordion.Body>
                    </Accordion.Item>
                ))}
            </Accordion>
        </div>
    );
};

export default DoctorReportList;
