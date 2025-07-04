import { useEffect, useState } from "react";
import { apiRequest, DataTable } from "@afyaquik/shared";
import { useParams } from "react-router-dom";

const columns = [
    { header: '#', accessor: 'id' },
    { header: 'Drug Name', accessor: 'drugName' },
    { header: 'Quantity', accessor: 'quantity' },
    { header: 'Dosage Instructions', accessor: 'dosageInstructions' },
    { header: 'Dispensed', accessor: 'dispensed', type: 'boolean' }
];

const searchFields = [
    {
        name: 'drugName',
        label: 'Drug Name',
    },
    {
        name: 'dispensed',
        label: 'Dispensed Status',
    }
];

const PatientDrugList = ({visitId, data}:{visitId:number, data: any}) => {
    return (
        <DataTable
            title="Patient Drugs"
            columns={columns}
            detailsView="index.html#/patient-drugs/#id/details"
            editView="index.html#/patient-drugs/#id/edit"
            searchFields={searchFields}
            searchEntity="patientDrugs"
            combinedSearchFieldsAndTerms={`patientVisitId=${visitId}`}
            dataEndpoint={`/search`}
            data={data}
            addTitle="Add Drug"
            addView={`index.html#/patient-drugs/add/${visitId}`}
        />
    );
};

export default PatientDrugList;
