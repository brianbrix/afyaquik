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

const DoctorReportList: React.FC<{ reports: DoctorReport[] }> = ({ reports }) => {
    // Function to print all reports
    const printAllReports = () => {
        const printContent = `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <title>${reports[0]?.patientName+';'+new Date().toLocaleString()}</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .report { margin-bottom: 30px; border: 1px solid #ddd; padding: 15px; page-break-after: always; }
                        .report-header { margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
                        .report-item { margin-bottom: 15px; }
                        .report-item-header { font-weight: bold; background-color: #f5f5f5; padding: 8px; margin-bottom: 5px; }
                        .print-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
                        @page { size: auto; margin: 10mm; }
                        @media print {
                            body { margin: 0; padding: 0; }
                            .print-header { position: fixed; top: 0; width: 100%; }
                        }
                    </style>
                </head>
                <body>
                    <div class="print-header">
                        <h1>Medical Reports</h1>
                        <div>Printed on: ${new Date().toLocaleString()}</div>
                    </div>
                    ${reports.map(report => `
                        <div class="report">
                            <div class="report-header">
                                <h2>Report #${report.id}</h2>
                                <p><strong>Patient:</strong> ${report.patientName}</p>
                                <p><strong>Doctor:</strong> ${report.doctorName}</p>
                                <p><strong>Station:</strong> ${report.station}</p>
                                <p><strong>Created:</strong> ${formatDate(report.createdAt)}</p>
                            </div>
                            
                            ${report.observationReportItems?.length ? `
                                <h3>Observation Reports</h3>
                                ${report.observationReportItems.map(item => `
                                    <div class="report-item">
                                        <div class="report-item-header">${item.itemName}</div>
                                        <div>${item.value}</div>
                                        ${item.comment ? `<div><strong>Comment:</strong> ${item.comment}</div>` : ''}
                                    </div>
                                `).join('')}
                            ` : ''}
                            
                            ${report.treatmentPlanReportItems?.length ? `
                                <h3>Treatment Plans</h3>
                                ${report.treatmentPlanReportItems.map(item => `
                                    <div class="report-item">
                                        <div class="report-item-header">${item.treatmentPlanItemName}</div>
                                        <div>${item.reportDetails}</div>
                                    </div>
                                `).join('')}
                            ` : ''}
                            
                            ${!report.observationReportItems?.length && !report.treatmentPlanReportItems?.length ?
            '<p class="text-muted">No items in this report.</p>' : ''}
                        </div>
                    `).join('')}
                </body>
            </html>
        `;

        const printWindow = window.open('', '_blank');
        if (printWindow) {
            printWindow.document.open();
            printWindow.document.write(printContent);
            printWindow.document.close();

            // Wait for content to load before printing
            printWindow.onload = () => {
                setTimeout(() => {
                    printWindow.print();
                    // Optional: close the window after printing
                    printWindow.close();
                }, 500);
            };
        }
    };

    // Function to print a single report
    const printSingleReport = (report: DoctorReport) => {
        const printContent = `
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <title>Medical Report #${report.id} for ${report.patientName}</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .report-header { margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
                        .report-item { margin-bottom: 15px; }
                        .report-item-header { font-weight: bold; background-color: #f5f5f5; padding: 8px; margin-bottom: 5px; }
                        .print-header { display: flex; justify-content: space-between; margin-bottom: 20px; }
                        @page { size: auto; margin: 10mm; }
                        @media print {
                            body { margin: 0; padding: 0; }
                            .print-header { position: fixed; top: 0; width: 100%; }
                        }
                    </style>
                </head>
                <body>
                    <div class="print-header">
                        <h1>Medical Report</h1>
                        <div>Printed on: ${new Date().toLocaleString()}</div>
                    </div>
                    <div class="report">
                        <div class="report-header">
                            <h2>Report #${report.id}</h2>
                            <p><strong>Patient:</strong> ${report.patientName}</p>
                            <p><strong>Doctor:</strong> ${report.doctorName}</p>
                            <p><strong>Station:</strong> ${report.station}</p>
                            <p><strong>Created:</strong> ${formatDate(report.createdAt)}</p>
                        </div>
                        
                        ${report.observationReportItems?.length ? `
                            <h3>Observation Reports</h3>
                            ${report.observationReportItems.map(item => `
                                <div class="report-item">
                                    <div class="report-item-header">${item.itemName}</div>
                                    <div>${item.value}</div>
                                    ${item.comment ? `<div><strong>Comment:</strong> ${item.comment}</div>` : ''}
                                </div>
                            `).join('')}
                        ` : ''}
                        
                        ${report.treatmentPlanReportItems?.length ? `
                            <h3>Treatment Plans</h3>
                            ${report.treatmentPlanReportItems.map(item => `
                                <div class="report-item">
                                    <div class="report-item-header">${item.treatmentPlanItemName}</div>
                                    <div>${item.reportDetails}</div>
                                </div>
                            `).join('')}
                        ` : ''}
                        
                        ${!report.observationReportItems?.length && !report.treatmentPlanReportItems?.length ?
            '<p class="text-muted">No items in this report.</p>' : ''}
                    </div>
                </body>
            </html>
        `;

        const printWindow = window.open('', '_blank');
        if (printWindow) {
            printWindow.document.open();
            printWindow.document.write(printContent);
            printWindow.document.close();

            printWindow.onload = () => {
                setTimeout(() => {
                    printWindow.print();
                    // Optional: close the window after printing
                    // printWindow.close();
                }, 500);
            };
        }
    };

    return (
        <div>
            <div className="d-flex justify-content-end mb-3 no-print">
                <ButtonGroup>
                    <Button variant="outline-primary" onClick={printAllReports}>
                        Print All Reports
                    </Button>
                </ButtonGroup>
            </div>

            <Accordion defaultActiveKey="0" className="my-4">
                {reports.map((report, index) => (
                    <Accordion.Item eventKey={String(index)} key={report.id}>
                        <Accordion.Header>
                            <div className="w-100 d-flex justify-content-between">
                                <div>
                                    <strong>Patient:</strong> {report.patientName} <br/>
                                    <strong>Doctor:</strong> {report.doctorName}<br/>
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
                                    Print This Report
                                </Button>
                            </div>

                            {/* ... (keep the rest of your existing JSX) ... */}
                        </Accordion.Body>
                    </Accordion.Item>
                ))}
            </Accordion>
        </div>
    );
};

export default DoctorReportList;
