import { useParams } from "react-router-dom";
import {DetailsPage, apiRequest, useAlert} from "@afyaquik/shared";
import { Button, Table } from "react-bootstrap";
import React, { useEffect, useState } from "react";

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
                onClick={() => window.location.href = `index.html#/patient-drugs`}
            >
                Go to Patient Drugs
            </Button>
            <Button
                variant="outline-primary"
                onClick={() => window.location.href = `index.html#/patient-drugs/${id}/edit`}
            >
                Edit
            </Button>
        </div>
    )
}

const PatientDrugDetailsPage = () => {
    let params = useParams();
    const { showAlert } = useAlert();
    const id = Number(params.id);
    const [drug, setDrug] = useState<PatientDrug | null>(null);
    const [drugs, setDrugs] = useState<PatientDrug[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const endpoint = `/patient-drugs/${id}`;
    const fields = [
        { label: "Drug Name", accessor: "drugName" },
        { label: "Quantity", accessor: "quantity" },
        { label: "Dosage Instructions", accessor: "dosageInstructions" },
        { label: "Dispensed", accessor: "dispensed", type: 'boolean' }
    ];

    useEffect(() => {
        // Fetch the individual drug details
        apiRequest(endpoint)
            .then(response => {
                setDrug(response);
                // Once we have the drug, fetch all drugs for this patient visit
                return apiRequest(`/patient-drugs/visit/${response.patientVisitId}`);
            })
            .then(response => {
                setDrugs(response);
            })
            .catch(error => {
                console.error('Error:', error);
                setError("Failed to fetch drug details");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [endpoint]);

    const handleDispense = (drugId: number) => {
        apiRequest(`/patient-drugs/${drugId}/dispense`, {
            method: 'PUT'
        })
        .then(response => {
            showAlert('Drug dispensed successfully', 'Drug Dispense', 'success')
            // Update the drugs list
            setDrugs(drugs.map(d =>
                d.id === drugId ? { ...d, dispensed: true } : d
            ));
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert(error.message,'Drug dispense error','error')
        });
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!drug) return <div>No drug found</div>;

    return (
        <div className="container my-4">
            <DetailsPage
                topComponents={[components(id)]}
                title={"Patient Drug Details"}
                endpoint={endpoint}
                fields={fields}
            />

            <div className="mt-5">
                <h5 className="text-primary fw-semibold mb-3">All Drugs for this Patient Visit</h5>
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
        </div>
    )
}

export default PatientDrugDetailsPage;
