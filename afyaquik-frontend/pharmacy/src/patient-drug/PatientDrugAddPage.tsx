import React, { useEffect, useState } from "react";
import { apiRequest, FieldConfig, StepConfig, StepForm } from "@afyaquik/shared";
import { multiSelectorWysiwygConfig } from "@afyaquik/shared/dist/StepConfig";
import { useParams } from "react-router-dom";

const PatientDrugAddPage = () => {
    const { patientVisitId } = useParams();
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [drugs, setDrugs] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [bulkMode, setBulkMode] = useState(false);
    const [selectedDrugId, setSelectedDrugId] = useState<number | null>(null);
    const [defaultPrice, setDefaultPrice] = useState<number | null>(null);

    useEffect(() => {
        if (!patientVisitId) {
            setLoading(false);
            return;
        }

        apiRequest('/drugs', { method: 'GET' })
            .then((response) => {
                if (response?.results?.content) {
                    setDrugs(response.results.content);
                } else {
                    setDrugs([]);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                setError("Failed to fetch drugs");
            })
            .finally(() => setLoading(false));
    }, [patientVisitId]);

    // Update default price when drug selection changes
    useEffect(() => {
        if (selectedDrugId) {
            const selectedDrug = drugs.find(drug => drug.id === selectedDrugId);
            if (selectedDrug) {
                setDefaultPrice(selectedDrug.currentPrice);
            }
        } else {
            setDefaultPrice(null);
        }
    }, [selectedDrugId, drugs]);

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
                options: drugOptions,
                onChange: (value: number) => setSelectedDrugId(value) // Add onChange handler
            },
            {
                name: "quantity",
                label: "Quantity",
                type: "number",
                step: "0.01",
                required: true
            },
            {
                name: "price",
                label: "Price",
                type: "number",
                step: "0.01",
                defaultValue: defaultPrice
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
                <div className="mb-3" key="mode-toggle">
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
    }, [drugs, bulkMode, defaultPrice]);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!patientVisitId) return <div>Patient Visit ID is required</div>;

    return (
        <div>
            {formConfig && (
                <StepForm
                    config={[formConfig]}
                    onSubmit={(data) => {
                        const endpoint = bulkMode ? '/patient-drugs/bulk' : '/patient-drugs';
                        const body = bulkMode
                            ? data.patientDrugs?.map((drug: any) => ({
                            drugId: parseInt(drug.drugId),
                            dosageInstructions: drug.dosageInstructions,
                            quantity: data.quantity || 1,
                            dispensed: false,
                            patientVisitId: Number(patientVisitId)
                        })) || []
                            : {
                                ...data,
                                patientVisitId: Number(patientVisitId)
                            };

                        apiRequest(endpoint, {
                            method: "POST",
                            body
                        })
                            .then(() => {
                                window.location.href = `index.html#/visit/${patientVisitId}/details?tab=Drugs`;
                            })
                            .catch(console.error);
                    }}
                    defaultValues={{
                        dispensed: false,
                        quantity: 1,
                        price: defaultPrice
                    }}
                    submitButtonLabel={bulkMode ? "Add Drugs in Bulk" : "Add Drug"}
                />
            )}
        </div>
    );
};

export default PatientDrugAddPage;
