import React, { useEffect, useState } from "react";
import { apiRequest, FieldConfig, StepConfig, StepForm } from "@afyaquik/shared";
import {multiSelectorWysiwygConfig} from "@afyaquik/shared/dist/StepConfig";
import { useParams } from "react-router-dom";

const PatientDrugAddPage = () => {
    const { patientVisitId } = useParams();
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [drugs, setDrugs] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [bulkMode, setBulkMode] = useState(false);

    useEffect(() => {
        if (!patientVisitId) {
            setLoading(false);
            return;
        }

        // Fetch available drugs
        apiRequest('/drugs', { method: 'GET' })
            .then((response) => {
                if (response && response.results && response.results.content) {
                    setDrugs(response.results.content);
                } else {
                    setDrugs([]);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                setError("Failed to fetch drugs");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [patientVisitId]);

    useEffect(() => {
        if (drugs.length === 0) return;

        const drugOptions = drugs.map(drug => ({
            label: drug.name,
            value: drug.id
        }));

        const drugItems = drugs.map(drug => ({
            name: drug.name,
            value: drug.id
        }));

        const fields: FieldConfig[] = bulkMode ? [] : [
            {
                name: "drugId",
                label: "Drug",
                type: "select",
                required: true,
                options: drugOptions
            },
            {
                name: "quantity",
                label: "Quantity",
                type: "number",
                step:"0.01",
                required: true
            },
            {
                name: "dosageInstructions",
                label: "Dosage Instructions",
                type: "wysiwyg",
                required: true
            },
            {
                name: "dispensed",
                label: "Dispensed",
                type: "checkbox"
            }
        ];

        // For bulk mode, we need to add a quantity field
        // Since the multiSelectorWysiwygConfig doesn't directly support additional fields,
        // we'll add a quantity field to the form and then use it when adding each drug
        const bulkFields: FieldConfig[] = [
            {
                name: "quantity",
                label: "Default Quantity",
                type: "number",
                required: true
            }
        ];

        const multiSelectorConfig: multiSelectorWysiwygConfig = {
            title: "Add Multiple Drugs",
            selectLabel: "Select Drug",
            items: drugItems as any,
            addButtonLabel: "Add Drug",
            selectedItemName: "drugId",
            configName: "patientDrugs",
            inputValueName: "dosageInstructions"
        };

        const config: StepConfig = {
            label: bulkMode ? "Add Multiple Patient Drugs" : "Add Patient Drug",
            fields: bulkMode ? bulkFields : fields,
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
    }, [drugs, bulkMode]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!patientVisitId) {
        return <div>Patient Visit ID is required</div>;
    }

    return (
        <div>
            {formConfig && (
                <StepForm
                    config={[formConfig]}
                    onSubmit={(data) => {
                        if (bulkMode) {
                            // Process bulk drugs
                            const bulkDrugs = data.patientDrugs?.map((drug: any) => ({
                                drugId: parseInt(drug.drugId),
                                dosageInstructions: drug.dosageInstructions,
                                quantity: data.quantity || 1, // Use the quantity from the form or default to 1
                                dispensed: false,
                                patientVisitId: Number(patientVisitId)
                            })) || [];

                            apiRequest('/patient-drugs/bulk', {
                                method: "POST",
                                body: bulkDrugs
                            }).then(
                                (response) => {
                                    console.log("Bulk Response ", response);
                                    window.location.href = `index.html#/patient-drugs/visit/${patientVisitId}`;
                                }
                            ).catch((error) => {
                                console.error("Error ", error);
                            });
                        } else {
                            // Process single drug
                            apiRequest('/patient-drugs', {
                                method: "POST",
                                body: {
                                    ...data,
                                    patientVisitId: Number(patientVisitId)
                                }
                            }).then(
                                (response) => {
                                    console.log("Response ", response);
                                    window.location.href = `index.html#/patient-drugs/visit/${patientVisitId}`;
                                }
                            ).catch((error) => {
                                console.error("Error ", error);
                            });
                        }
                    }}
                    defaultValues={{
                        dispensed: false,
                        quantity:1
                    }}
                    submitButtonLabel={bulkMode ? "Add Drugs in Bulk" : "Add Drug"}
                />
            )}
        </div>
    );
}

export default PatientDrugAddPage;
