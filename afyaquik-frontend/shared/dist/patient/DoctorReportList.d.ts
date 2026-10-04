import React from 'react';
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
declare const DoctorReportList: React.FC<{
    reports: DoctorReport[];
    title?: string;
}>;
export default DoctorReportList;
