import { useEffect, useState } from "react";
import {apiRequest, DataTable} from "@afyaquik/shared";

interface AuthResponse {
    id: number;
}

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Visit Type', accessor: 'visitType' },
    { header: 'Date of Visit', accessor: 'visitDate', type: 'datetime' }
];

const searchFields = [
    {
        name: 'createdAt',
        label: 'When added',
    },
    {
        name: 'visitType',
        label: 'Visit Type',
    },
    {
        name: 'visitStatus',
        label: 'Visit Status',
    },
    {
        name: 'visitDate',
        label: 'Visit Date',
    }
];

const DoctorPatientVisitList = () => {
    const [patientVisits, setPatientVisits] = useState([]);
    const [assignedDoctorId, setAssignedDoctorId] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchDoctorId = async () => {
            try {
                const response: AuthResponse = await apiRequest(`/users/me`);
                setAssignedDoctorId(response.id);
            } catch (err) {
                setError("Failed to fetch doctor information");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDoctorId();
    }, []);

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!assignedDoctorId) return <div>No doctor information</div>;

    return (
        <DataTable
            title="Visits assigned to me"
            columns={columns}
            data={patientVisits}
            // editView="index.html#/visits/#id/edit"
            detailsView="index.html#/visits/#id/details"
            combinedSearchFieldsAndTerms={`patientAttendingPlan.assignedOfficer.id=${assignedDoctorId}`}
            searchFields={searchFields}
            searchEntity="visits"
            dateFieldName={"patientAttendingPlan.updatedAt"}
            requestMethod="POST"
            dataEndpoint="/search"
        />
    );
};

export default DoctorPatientVisitList;
