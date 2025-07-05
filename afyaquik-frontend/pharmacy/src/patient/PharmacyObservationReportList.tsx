import React, { useEffect, useState } from 'react';
import { DoctorReportList, apiRequest } from '@afyaquik/shared';

interface ObservationReportItem {
    id: number;
    itemId: number;
    itemName: string;
    reportId: number;
    value: string;
    comment?: string | null;
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
}

interface PharmacyObservationReportListProps {
    visitId: number;
}

const PharmacyObservationReportList: React.FC<PharmacyObservationReportListProps> = ({ visitId }) => {
    const [reports, setReports] = useState<DoctorReport[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (visitId) {
            setLoading(true);
            apiRequest(`/observations/visit/${visitId}?page=0&size=100&sort=createdAt,desc`)
                .then(response => {
                    setReports(response.results.content);
                })
                .catch(err => {
                    console.error('Error fetching observation reports:', err);
                    setError('Failed to load observation reports');
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }, [visitId]);

    if (loading) return <div>Loading observation reports...</div>;
    if (error) return <div>Error: {error}</div>;
    if (reports.length === 0) return <div>No observation reports found</div>;

    return <DoctorReportList reports={reports} />;
};

export default PharmacyObservationReportList;
