import { useEffect, useState } from "react";
import { apiRequest, DataTable } from "@afyaquik/shared";

const columns = [

    { header: 'Patient Name', accessor: 'patientName' },
    { header: 'Visit Date', accessor: 'visitDate', type: 'date' },
    { header: 'Status', accessor: 'status' },
    { header: 'Next Station', accessor: 'nextStation' },
    { header: 'Assigned Officer', accessor: 'assignedOfficer' }
];

const searchFields = [
    {
        name: 'patientVisit.patient.firstName',
        label: 'Patient First Name',
    },
    {
        name: 'patientVisit.patient.lastName',
        label: 'Patient Last Name',
    },
    {
        name: 'assignedOfficer.username',
        label: 'Assigned Officer',
    }
];

const PatientAssignmentList = ({query}:{query?:string}) => {

    const userId = Number(localStorage.getItem("userId"));
    if (!query) {
        query = `nextStation.name=PHARMACY,assignedOfficer.id=${userId}`
    }
    else {
        query = `${query},nextStation.name=PHARMACY,assignedOfficer.id=${userId}`
    }

    console.log("Assignments query", query)
    return (
        <DataTable
            title="Pharmacy Assignments"
            columns={columns}
            detailsView="index.html#/assignments/#id/details"
            editView="index.html#/assignments/#id/edit"
            searchFields={searchFields}
            searchEntity="patientAssignments"
            combinedSearchFieldsAndTerms={query}
            isSearchable={true}
            addTitle="Add Assignment"
            addView="index.html#/assignments/add"
            dataEndpoint={'/search'}

        />
    );
};

export default PatientAssignmentList;
