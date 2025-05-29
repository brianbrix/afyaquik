import React from 'react';
import { Accordion, Card, Badge } from 'react-bootstrap';
import formatDate from "../dateFormatter";

interface ReportItem {
    id: number;
    itemId: number;
    itemName: string;
    reportId: number;
    value: string;
    comment?: string | null;
}

interface ObservationReport {
    id: number;
    patientVisitId: number;
    patientName: string;
    station: string;
    doctorId: number;
    doctorName: string;
    createdAt: string;
    updatedAt: string;
    items: ReportItem[];
}

const ObservationReportList: React.FC<{ reports: ObservationReport[] }> = ({ reports }) => {
    return (
        <Accordion defaultActiveKey="0" className="my-4">
            {reports.map((report, index) => (
                <Accordion.Item eventKey={String(index)} key={report.id}>
                    <Accordion.Header>
                        <div className="w-100 d-flex justify-content-between">
                            <div>
                                <strong>Patient:</strong> {report.patientName} <br />
                                <strong>Doctor:</strong> {report.doctorName}
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
                        {report.items.length === 0 ? (
                            <p className="text-muted">No items in this report.</p>
                        ) : (
                            report.items.map((item) => (
                                <Card key={item.id} className="mb-3 shadow-sm border-0">
                                    <Card.Header className="fw-semibold bg-light">
                                        {item.itemName}
                                    </Card.Header>
                                    <Card.Body>
                                        <div dangerouslySetInnerHTML={{ __html: item.value }} />
                                        {item.comment && (
                                            <div className="mt-2">
                                                <strong>Comment:</strong> {item.comment}
                                            </div>
                                        )}
                                    </Card.Body>
                                </Card>
                            ))
                        )}
                    </Accordion.Body>
                </Accordion.Item>
            ))}
        </Accordion>
    );
};

export default ObservationReportList;
