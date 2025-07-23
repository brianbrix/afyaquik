import React, {useEffect, useRef, useState} from "react";
import { apiRequest, FieldConfig, StepConfig, StepForm } from "@afyaquik/shared";
import { multiSelectorWysiwygConfig } from "@afyaquik/shared/dist/StepConfig";
import { useParams } from "react-router-dom";

const PatientDrugAddPage = () => {
    const formRef = useRef<any>(null);
    const { patientVisitId } = useParams();
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [drugs, setDrugs] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [bulkMode, setBulkMode] = useState(false);
    const [selectedDrugId, setSelectedDrugId]= useState(0);
    const [defaultValues, setDefaultValues] = useState({
        dispensed: false,
        quantity: 1,
        price: null as number | null,
        drugId: null as number | null
    });

    useEffect(() => {
        if (!patientVisitId) {
            setLoading(false);
            return;
        }

        apiRequest('/drugs?enabled=true', { method: 'GET' })
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

    // Update default price when drugId changes
    useEffect(() => {
        if (selectedDrugId && !bulkMode) {
            const selectedDrug = drugs.find(drug => drug.id == selectedDrugId);
            if (selectedDrug && formRef.current) {
                formRef.current.setValue("price", selectedDrug.currentPrice || 0.00);
            }
        }
    }, [selectedDrugId, drugs, bulkMode]);

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
                onChange: (value)=>setSelectedDrugId(value)
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
                label: "Total Price",
                type: "number",
                step: "0.01"
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
                        onClick={() => {
                            setBulkMode(!bulkMode);
                            setDefaultValues({
                                dispensed: false,
                                quantity: 1,
                                price: null,
                                drugId: null
                            });
                        }}
                    >
                        {bulkMode ? "Switch to Single Drug Mode" : "Switch to Bulk Mode"}
                    </button>
                </div>
            ],
            multiSelectorWysiwygConfigs: bulkMode ? [multiSelectorConfig] : undefined
        };

        setFormConfig(config);
    }, [drugs, bulkMode]);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;
    if (!patientVisitId) return <div>Patient Visit ID is required</div>;

    return (
        <div>
            {formConfig && (
                <StepForm
                    config={[formConfig]}
                    formMethodsRef={formRef}
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
                                window.location.href = `index.html#/visits/${patientVisitId}/details?tab=Drugs`;
                            })
                            .catch(console.error);
                    }}
                    defaultValues={defaultValues}
                    submitButtonLabel={bulkMode ? "Add Drugs in Bulk" : "Add Drug"}
                    key={bulkMode ? 'bulk' : 'single'}

                />
            )}
        </div>
    );
};

export default PatientDrugAddPage;
