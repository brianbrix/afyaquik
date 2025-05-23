import React, { useEffect, useState } from 'react';
import apiRequest from './api';
import { Card, Col, Row, Spinner, Tab, Tabs } from 'react-bootstrap'; // Add Tabs components
import formatDate from "./dateFormatter";

interface DetailsPageProps {
    fields: { label: string; accessor: string; type?: string}[];
    endpoint: string;
    title?: string;
    otherComponentsToRender?: { title: string; content: React.ReactNode }[]; // Changed structure
    topComponents?: React.ReactNode[];
}

const DetailsPage: React.FC<DetailsPageProps> = ({ fields, endpoint, title, otherComponentsToRender, topComponents }) => {
    const [record, setRecord] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState<string | null>(null);

    useEffect(() => {
        apiRequest(endpoint)
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

    useEffect(() => {
        if (otherComponentsToRender?.length) {
            setActiveTab(otherComponentsToRender[0].title);
        }
    }, [otherComponentsToRender]);

    if (loading) return <div className="text-center py-5"><Spinner animation="border" /></div>;
    if (!record) return <div className="text-danger text-center py-5">Record not found.</div>;

    return (
        <div className="container py-5">
            {topComponents && topComponents.length > 0 && (
                <Row className="g-3">
                    {topComponents.map((component, idx) => (
                        <Col key={idx} md={12}>
                            {component}
                        </Col>
                    ))}
                </Row>
            )}

            <Card className="shadow-sm">
                <Card.Header className="bg-primary text-white">
                    <h5 className="mb-0">{title ? title : "Details"}</h5>
                </Card.Header>
                <Card.Body>
                    <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                        {fields.map(({ label, accessor, type }) => {
                            const value = resolveValue(record, accessor);

                            return (
                                <div key={accessor} className="col">
                                    <div className="p-2 border rounded h-100">
                                        <div className="text-truncate" title={label}>
                                            <strong>{label}:</strong>
                                        </div>
                                        {type === 'wysiwyg' ? (
                                            <div dangerouslySetInnerHTML={{ __html: value || '' }} />
                                        ) : (
                                            <div className="text-truncate" title={value}>
                                                {type === 'date' || type === 'datetime' ? formatDate(value) : value}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </Card.Body>
            </Card>

            {otherComponentsToRender && otherComponentsToRender.length > 0 && (
                <Card className="mt-4 shadow-sm">
                    <Card.Body>
                        <Tabs
                            activeKey={activeTab || ''}
                            onSelect={(k) => setActiveTab(k)}
                            className="mb-3"
                            id="details-page-tabs"
                        >
                            {otherComponentsToRender.map(({ title, content }) => (
                                <Tab
                                    key={title}
                                    eventKey={title}
                                    title={title}
                                >
                                    <div className="mt-3">
                                        {content}
                                    </div>
                                </Tab>
                            ))}
                        </Tabs>
                    </Card.Body>
                </Card>
            )}
        </div>
    );
};

function resolveValue(obj: any, path: string): any {
    return path.split('.').reduce((acc, key) => acc?.[key], obj) ?? 'N/A';
}

export default DetailsPage;
