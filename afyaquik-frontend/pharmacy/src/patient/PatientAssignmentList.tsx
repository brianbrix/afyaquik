import { useEffect, useState } from "react";
import { apiRequest, DataTable } from "@afyaquik/shared";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Visit Date', accessor: 'visitDate', type: 'date' },
    { header: 'Status', accessor: 'status' },
    { header: 'Next Station', accessor: 'nextStation' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' }
];

const searchFields = [
    {
        name: 'patientName',
        label: 'Patient Name',
    },
    {
        name: 'status',
        label: 'Status',
    },
    {
        name: 'assignedOfficer',
        label: 'Assigned Officer',
    }
];

const PatientAssignmentList = () => {
    const userId = Number(localStorage.getItem("userId"));
    const [initialQuery, setInitialQuery] = useState("");

    useEffect(() => {
        if (userId) {
            // Set initial query to filter patient assignments where nextStation is PHARMACY and assigned officer is current user
            setInitialQuery(`nextStation.name=PHARMACY,assignedOfficer.id=${userId}`);
        }
    }, [userId]);

    return (
        <DataTable
            title="Pharmacy Assignments"
            columns={columns}
            detailsView="index.html#/assignments/#patientVisitId/details"
            editView="index.html#/assignments/#patientVisitId/edit"
            searchFields={searchFields}
            searchEntity="patientAssignments"
            combinedSearchFieldsAndTerms={initialQuery}
            isSearchable={true}
            addTitle="Add Assignment"
            addView="index.html#/assignments/add"
        />
    );
};

export default PatientAssignmentList;
