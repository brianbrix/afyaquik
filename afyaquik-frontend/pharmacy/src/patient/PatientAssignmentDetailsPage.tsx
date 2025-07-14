import { useParams, useLocation } from "react-router-dom";
import { DetailsPage, apiRequest, DataTable } from "@afyaquik/shared";
import { Button } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import PatientDrugList from "../patient-drug/PatientDrugList";
import PharmacyObservationReportList from "./PharmacyObservationReportList";
import PharmacyTreatmentPlanListPage from "./PharmacyTreatmentPlanListPage";

interface PatientAssignment {
    id: number;
    patientName: string;
    patientVisitId: number;
    attendingOfficerUserName: string;
    assignedOfficer: string;
    nextStation: string;
    status: string;
}

interface Assignment {
    id: number;
    patientVisitId:number;
    patientName: string;
    visitDate: string;
    status: string;
    assignedOfficer: string;
    notes: string;
}

interface PatientDrug {
    id: number;
    drugName: string;
    quantity: number;
    dosageInstructions: string;
    dispensed: boolean;
    patientVisitId: number;
}

const components = function (id: any) {
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html#/assignments`}
            >
                Go to Assignments
            </Button>
        </div>
    )
}

const PatientAssignmentDetailsPage = () => {
    let params = useParams();
    const location = useLocation();
    const id = Number(params.id);
    const [visit, setVisit] = useState<Assignment | null>(null);
    const [drugs, setDrugs] = useState<PatientDrug[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const endpoint = `/patient/visits/assignments/${id}`;

    // Parse the query string to get the tab parameter
    const searchParams = new URLSearchParams(location.search);
    const tabParam = searchParams.get('tab');
    const fields = [
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Date", accessor: "visitDate", type: 'date' },
        { label: "Status", accessor: "status" },
        { label: "Assigned To", accessor: "assignedOfficer" },
        { label: "Notes", accessor: "notes" }
    ];

    useEffect(() => {
        // Fetch the individual visit details
        apiRequest(endpoint)
            .then(response => {
                setVisit(response);
                // Once we have the visit, fetch all drugs for this patient visit
                return apiRequest(`/patient-drugs/visit/${response.patientVisitId}`);
            })
            .then(response => {
                setDrugs(response);
            })
            .catch(error => {
                console.error('Error:', error);
                setError("Failed to fetch visit details");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [endpoint]);

    // Define columns for the DataTable
    const drugColumns = [
        { header: 'ID', accessor: 'id' },
        { header: 'Drug Name', accessor: 'drugName' },
        { header: 'Quantity', accessor: 'quantity' },
        { header: 'Dosage Instructions', accessor: 'formattedDosageInstructions' },
        { header: 'Dispensed', accessor: 'dispensed' }
    ];


    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!visit) return <div>No visit found</div>;

    return (drugs && visit?
            <DetailsPage
                topComponents={[components(id)]}
                title={"Patient Assignment Details"}
                endpoint={endpoint}
                fields={fields}
                initialActiveTab={tabParam || undefined}
                otherComponentsToRender={[
                    {title:'Observation Report',content: <PharmacyObservationReportList visitId={visit.patientVisitId} title="Observation Report"/>},
                    {title:'Treatment Plan Report', content: <PharmacyTreatmentPlanListPage visitId={visit.patientVisitId} title="Treatment Plan Report"/>},
                    {title:"Drugs", content: <PatientDrugList data={drugs} visitId={visit.patientVisitId}/>}
                ]}
            />:<div>Drugs not loaded</div>
    )
}

export default PatientAssignmentDetailsPage;
