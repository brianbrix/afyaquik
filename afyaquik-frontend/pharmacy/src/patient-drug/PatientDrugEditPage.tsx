import React, { useEffect, useState } from "react";
import { apiRequest, FieldConfig, StepConfig, StepForm } from "@afyaquik/shared";
import { multiSelectorWysiwygConfig } from "@afyaquik/shared/dist/StepConfig";
import { useParams } from "react-router-dom";

interface PatientDrug {
    id: number;
    drugName: string;
    quantity: number;
    dosageInstructions: string;
    dispensed: boolean;
    patientVisitId: number;
    drugId: number;
}

const PatientDrugEditPage = () => {
    const { id } = useParams();
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [patientDrug, setPatientDrug] = useState<PatientDrug | null>(null);
    const [patientVisitDrugs, setPatientVisitDrugs] = useState<PatientDrug[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [bulkMode, setBulkMode] = useState(false);

    useEffect(() => {
        if (!id) {
            setLoading(false);
            return;
        }

        apiRequest(`/patient-drugs/${id}`, { method: 'GET' })
            .then((response) => {
                setPatientDrug(response);
                // Once we have the drug, fetch all drugs for this patient visit
                return apiRequest(`/patient-drugs/visit/${response.patientVisitId}`);
            })
            .then((drugsResponse) => {
                setPatientVisitDrugs(drugsResponse);
            })
            .catch(error => {
                console.error('Error:', error);
                setError("Failed to fetch patient drug");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [id]);

    useEffect(() => {
        if (!patientDrug || patientVisitDrugs.length === 0) return;

        // For single mode, we just need fields for the current drug
        const singleFields: FieldConfig[] = [
            {
                name: "quantity",
                label: "Quantity",
                type: "number",
                step:"0.01",
                required: true,
                disabled: patientDrug.dispensed
            },
            {
                name: "dosageInstructions",
                label: "Dosage Instructions",
                type: "wysiwyg",
                required: true,
            },
            {
                name: "dispensed",
                label: "Dispensed",
                type: "checkbox",
                disabled: patientDrug.dispensed
            }
        ];

        // For bulk mode, we need a default quantity field
        const bulkFields: FieldConfig[] = [
            {
                name: "defaultQuantity",
                label: "Default Quantity (applies to all drugs)",
                type: "number",
                required: true,
            }
        ];

        // Create items for the multi-selector
        const drugItems = patientVisitDrugs.map(drug => ({
            name: drug.drugName,
            value: drug.id.toString(),
            // Pre-select the current drug
            selected: drug.id === patientDrug.id
        }));

        // Create multi-selector config for bulk mode
        const multiSelectorConfig: multiSelectorWysiwygConfig = {
            title: "Edit Multiple Drugs",
            selectLabel: "Select Drug",
            items: drugItems as any,
            addButtonLabel: "Add Drug to Edit",
            selectedItemName: "drugId",
            configName: "drugsToEdit",
            inputValueName: "dosageInstructions"
        };

        const config: StepConfig = {
            label: bulkMode ? "Edit Multiple Patient Drugs" : "Edit Patient Drug",
            fields: bulkMode ? bulkFields : singleFields,
            stepButtonLabel: "Save",
            topComponents: [
                <div className="mb-3">
                    <button
                        type="button"
                        className="btn btn-outline-primary"
                        onClick={() => setBulkMode(!bulkMode)}
                    >
                        {bulkMode ? "Switch to Single Drug Mode" : "Switch to Bulk Mode"}
                    </button>
                </div>
            ],
            multiSelectorWysiwygConfigs: bulkMode ? [multiSelectorConfig] : undefined
        };

        setFormConfig(config);
    }, [patientDrug, patientVisitDrugs, bulkMode]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div>
            {formConfig && patientDrug && (
                <StepForm
                    config={[formConfig]}
                    onSubmit={(data) => {
                        if (bulkMode) {
                            // In bulk mode, we need to update each selected drug
                            const updatePromises = data.drugsToEdit?.map((drugToEdit: any) => {
                                // Find the original drug in the patientVisitDrugs array
                                const originalDrug = patientVisitDrugs.find(d => d.id.toString() === drugToEdit.drugId);

                                if (!originalDrug) {
                                    console.error(`Drug with ID ${drugToEdit.drugId} not found`);
                                    return Promise.reject(`Drug with ID ${drugToEdit.drugId} not found`);
                                }

                                // Create the updated drug object
                                const updatedDrug = {
                                    ...originalDrug,
                                    quantity: data.defaultQuantity || originalDrug.quantity,
                                    dosageInstructions: drugToEdit.dosageInstructions || originalDrug.dosageInstructions
                                };

                                // Make the API call to update this drug
                                return apiRequest(`/patient-drugs/${drugToEdit.drugId}`, {
                                    method: "PUT",
                                    body: updatedDrug
                                });
                            }) || [];

                            // Wait for all updates to complete
                            Promise.all(updatePromises)
                                .then(responses => {
                                    console.log("All drugs updated successfully", responses);
                                    // Redirect to the details page of the original drug
                                    window.location.href = `index.html#/patient-drugs/${id}/details`;
                                })
                                .catch(error => {
                                    console.error("Error updating drugs", error);
                                    alert("An error occurred while updating the drugs. Please try again.");
                                });
                        } else {
                            // In single mode, just update the current drug
                            apiRequest(`/patient-drugs/${id}`, {
                                method: "PUT",
                                body: {
                                    ...patientDrug,
                                    ...data
                                }
                            }).then(
                                (response) => {
                                    console.log("Response ", response);
                                    window.location.href = `index.html#/patient-drugs/${id}/details`;
                                }
                            ).catch((error) => {
                                console.error("Error ", error);
                                alert("An error occurred while updating the drug. Please try again.");
                            });
                        }
                    }}
                    defaultValues={bulkMode ? {
                        defaultQuantity: patientDrug.quantity
                    } : {
                        quantity: patientDrug.quantity,
                        dosageInstructions: patientDrug.dosageInstructions,
                        dispensed: patientDrug.dispensed
                    }}
                    submitButtonLabel={bulkMode ? "Save All Changes" : "Save Changes"}
                />
            )}
        </div>
    );
}

export default PatientDrugEditPage;
