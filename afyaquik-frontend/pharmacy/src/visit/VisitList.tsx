import { useEffect, useState } from "react";
import { apiRequest, DataTable } from "@afyaquik/shared";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Visit Date', accessor: 'visitDate', type: 'date' },
    { header: 'Status', accessor: 'status' }
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
        name: 'patientAssignments.assignedOfficer.username',
        label: 'Assigned Officer',
    }
];

const VisitList = () => {
    const  userId  = Number(localStorage.getItem("userId"));
    const [initialQuery, setInitialQuery] = useState("");

    useEffect(() => {
        if (userId) {
            // Set initial query to filter patient assignments where nextStation is PHARMACY and assigned officer is current user
            setInitialQuery(`patientAssignments.nextStation.name=PHARMACY,patientAssignments.assignedOfficer.id=${userId}`);
        }
    }, [userId]);

    return (
        <DataTable
            title="Patient Visits"
            columns={columns}
            detailsView="index.html#/visits/#patientVisitId/details"
            editView="index.html#/visits/#patientVisitId/assign"
            editTitle={"Assign Visit"}
            searchFields={searchFields}
            searchEntity="visits"
            combinedSearchFieldsAndTerms={initialQuery}
            isSearchable={true}
            addView="index.html#/visits/add"
        />
    );
};

export default VisitList;
