import React, { useEffect, useState } from 'react';
import apiRequest from './api';
import { Card, Spinner } from 'react-bootstrap';

interface DetailsPageProps {
    fields: { label: string; accessor: string }[];
    endpoint: string;
    listRender?: React.ReactNode;
}

const DetailsPage: React.FC<DetailsPageProps> = ({ fields, endpoint,listRender }) => {
    const [record, setRecord] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        apiRequest(endpoint)
            .then(setRecord)
            .catch(err => console.error(err))
            .finally(() => setLoading(false));
    }, [endpoint]);

    if (loading) return <div className="text-center py-5"><Spinner animation="border" /></div>;
    if (!record) return <div className="text-danger text-center py-5">Record not found.</div>;

    return (
        <div className="container py-5">
            <Card className="shadow-sm">
                <Card.Header className="bg-primary text-white">
                    <h5 className="mb-0">Details</h5>
                </Card.Header>
                <Card.Body>
                    <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                        {fields.map(({ label, accessor }) => (
                            <div key={accessor} className="col">
                                <div className="p-2 border rounded h-100">
                                    <div className="text-truncate" title={label}>
                                        <strong>{label}:</strong>
                                    </div>
                                    <div className="text-truncate" title={resolveValue(record, accessor)}>
                                        {resolveValue(record, accessor)}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </Card.Body>
            </Card>
            {listRender && (
                <div className="mt-4">
                    <h6 className="text-muted">Records</h6>
                    {listRender}
                </div>
            )}
        </div>
    );
};

function resolveValue(obj: any, path: string): any {
    return path.split('.').reduce((acc, key) => acc?.[key], obj) ?? 'N/A';
}

export default DetailsPage;
