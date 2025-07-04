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

const PatientDrugList = () => {
    const { patientVisitId } = useParams();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    if (!patientVisitId) {
        return <div>Patient Visit ID is required</div>;
    }

    return (
        <DataTable
            title="Patient Drugs"
            columns={columns}
            detailsView="index.html#/patient-drugs/#id/details"
            editView="index.html#/patient-drugs/#id/edit"
            searchFields={searchFields}
            searchEntity="patient-drugs"
            combinedSearchFieldsAndTerms={`patientVisitId=${patientVisitId}`}
            dataEndpoint={`/patient-drugs/visit/${patientVisitId}`}
            addTitle="Add Drug"
            addView={`index.html#/patient-drugs/add/${patientVisitId}`}
        />
    );
};

export default PatientDrugList;
