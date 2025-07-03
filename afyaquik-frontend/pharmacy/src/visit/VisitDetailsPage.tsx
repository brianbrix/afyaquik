import { useParams } from "react-router-dom";
import { DetailsPage, apiRequest } from "@afyaquik/shared";
import { Button, Table } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import PatientAssignmentList from "../patient/PatientAssignmentList";

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
    const endpoint = `/visits/${id}`;
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

    const drugAssignments =()=>{
        return(
    <div className="mt-5">
        <h5 className="text-primary fw-semibold mb-3">Drugs for this Visit</h5>
        <Table bordered hover responsive className="table-sm align-middle">
            <thead className="table-light">
            <tr>
                <th>ID</th>
                <th>Drug Name</th>
                <th>Quantity</th>
                <th>Dosage Instructions</th>
                <th>Dispensed</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {drugs.length === 0 ? (
                <tr>
                    <td colSpan={6} className="text-center">No drugs found</td>
                </tr>
            ) : (
                drugs.map(drug => (
                    <tr key={drug.id}>
                        <td>{drug.id}</td>
                        <td>{drug.drugName}</td>
                        <td>{drug.quantity}</td>
                        <td dangerouslySetInnerHTML={{ __html: drug.dosageInstructions }}></td>
                        <td>{drug.dispensed ? 'Yes' : 'No'}</td>
                        <td>
                            {!drug.dispensed && (
                                <Button
                                    variant="success"
                                    size="sm"
                                    onClick={() => handleDispense(drug.id)}
                                >
                                    Dispense
                                </Button>
                            )}
                        </td>
                    </tr>
                ))
            )}
            </tbody>
        </Table>
    </div>
        )
    }
    const handleDispense = (drugId: number) => {
        apiRequest(`/patient-drugs/${drugId}/dispense`, {
            method: 'PUT'
        })
        .then(response => {
            alert('Drug dispensed successfully');
            // Update the drugs list
            setDrugs(drugs.map(d =>
                d.id === drugId ? { ...d, dispensed: true } : d
            ));
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while dispensing the drug');
        });
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!visit) return <div>No visit found</div>;

    return (
        <div className="container my-4">
            <DetailsPage
                topComponents={[components(id)]}
                title={"Visit Details"}
                endpoint={endpoint}
                fields={fields}
                otherComponentsToRender={[
                    {title:"Patient Assignments", content: <PatientAssignmentList />},
                    {title:"Drugs", content: drugAssignments()}
                ]}
            />
        </div>
    )
}

export default VisitDetailsPage;
