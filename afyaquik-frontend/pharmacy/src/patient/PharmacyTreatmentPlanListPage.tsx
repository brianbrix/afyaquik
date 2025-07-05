import React, { useEffect, useState } from 'react';
import { DoctorReportList, apiRequest } from '@afyaquik/shared';

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
    treatmentPlanReportItems?: TreatmentPlanReportItem[];
}

interface PharmacyTreatmentPlanListPageProps {
    visitId: number;
}

const PharmacyTreatmentPlanListPage: React.FC<PharmacyTreatmentPlanListPageProps> = ({ visitId }) => {
    const [reports, setReports] = useState<DoctorReport[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (visitId) {
            setLoading(true);
            apiRequest(`/plan/visit/${visitId}?page=0&size=100&sort=createdAt,desc`)
                .then(response => {
                    setReports(response.results.content);
                })
                .catch(err => {
                    console.error('Error fetching treatment plan reports:', err);
                    setError('Failed to load treatment plan reports');
                })
                .finally(() => {
                    setLoading(false);
                });
        }
    }, [visitId]);

    if (loading) return <div>Loading treatment plan reports...</div>;
    if (error) return <div>Error: {error}</div>;
    if (reports.length === 0) return <div>No treatment plan reports found</div>;

    return <DoctorReportList reports={reports} />;
};

export default PharmacyTreatmentPlanListPage;
