import { useParams } from "react-router-dom";
import { DetailsPage, apiRequest, DataTable } from "@afyaquik/shared";
import { Button } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import PatientAssignmentList from "../patient/PatientAssignmentList";
import PatientDrugList from "../patient-drug/PatientDrugList";

interface PatientAssignment {
    id: number;
    patientName: string;
    patientVisitId: number;
    attendingOfficerUserName: string;
    assignedOfficer: string;
    nextStation: string;
    status: string;
}

interface Visit {
    id: number;
    patientName: string;
    visitDate: string;
    status: string;
    assignedTo: string;
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
                onClick={() => window.location.href = `index.html#/visits`}
            >
                Go to Visits
            </Button>
            <Button
                variant="outline-primary"
                onClick={() => window.location.href = `index.html#/visits/${id}/edit`}
            >
                Edit
            </Button>
        </div>
    )
}

const VisitDetailsPage = () => {
    let params = useParams();
    const id = Number(params.id);
    const [visit, setVisit] = useState<Visit | null>(null);
    const [drugs, setDrugs] = useState<PatientDrug[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const endpoint = `/patient/visits/${id}`;
    const fields = [
        { label: "Patient Name", accessor: "patientName" },
        { label: "Visit Date", accessor: "visitDate", type: 'date' },
        { label: "Status", accessor: "status" },
        { label: "Assigned To", accessor: "assignedTo" },
        { label: "Notes", accessor: "notes" }
    ];

    useEffect(() => {
        // Fetch the individual visit details
        apiRequest(endpoint)
            .then(response => {
                setVisit(response);
                // Once we have the visit, fetch all drugs for this patient visit
                return apiRequest(`/patient-drugs/visit/${response.id}`);
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


    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!visit) return <div>No visit found</div>;

    return (
        drugs?
            <DetailsPage
                topComponents={[components(id)]}
                title={"Visit Details"}
                endpoint={endpoint}
                fields={fields}
                otherComponentsToRender={[
                    {title:"Patient Assignments", content: <PatientAssignmentList query={`patientVisitId=${visit.id}`} />},
                    {title:"Drugs", content: <PatientDrugList data={drugs} visitId={visit.id}/>}
                ]}
            />:<div>Drugs not loaded</div>
    )
}

export default VisitDetailsPage;
