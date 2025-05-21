import React, { useEffect, useState } from 'react';
import apiRequest from './api';
import {Card, Col, Row, Spinner} from 'react-bootstrap';
import formatDate from "./dateFormatter";

interface DetailsPageProps {
    fields: { label: string; accessor: string; type?: string}[];
    endpoint: string;
    title?:string;
    otherComponentsToRender?: React.ReactNode[];
    topComponents?: React.ReactNode[];
}

const DetailsPage: React.FC<DetailsPageProps> = ({ fields, endpoint,title,otherComponentsToRender,topComponents }) => {
    const [record, setRecord] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        apiRequest(endpoint)
            .then(response=>{
                if (endpoint.startsWith('/patients/'))
                {
                    let patientName = response.firstName;
                    if (response.lastName)
                        patientName += " "+response.lastName;
                    localStorage.setItem("patientName", patientName);
                }
                setRecord(response);
            })
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, [endpoint]);

    if (loading) return <div className="text-center py-5"><Spinner animation="border" /></div>;
    if (!record) return <div className="text-danger text-center py-5">Record not found.</div>;

    return (
        <div className="container py-5">
            {topComponents && topComponents.length > 0 &&(
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
                                        { type === 'wysiwyg' ? (
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
            {otherComponentsToRender && otherComponentsToRender.length > 0 &&(
                <div>
                    {otherComponentsToRender.map((component, idx) => (
                        <div className="mt-4">
                            {component}
                        </div>
                    ))}
                </div>

            )}


        </div>
    );
};

function resolveValue(obj: any, path: string): any {
    return path.split('.').reduce((acc, key) => acc?.[key], obj) ?? 'N/A';
}

export default DetailsPage;
