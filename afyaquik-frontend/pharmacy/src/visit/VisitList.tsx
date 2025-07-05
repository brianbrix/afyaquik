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
        name: 'firstName',
        label: 'Patient First Name',
    },
    {
        name: 'lastName',
        label: 'Patient Last Name',
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
    const query=`patientAssignments.nextStation.name=PHARMACY,patientAssignments.assignedOfficer.id=${userId}`;

    return (
        <DataTable
            title="Patient Visits"
            columns={columns}
            detailsView="index.html#/visits/#id/details"
            editView="index.html#/visits/#id/assign"
            editTitle={"Assign Visit"}
            searchFields={searchFields}
            searchEntity="visits"
            combinedSearchFieldsAndTerms={query}
            isSearchable={true}
            addView="index.html#/visits/add"
            dataEndpoint={'/search'}
        />
    );
};

export default VisitList;
