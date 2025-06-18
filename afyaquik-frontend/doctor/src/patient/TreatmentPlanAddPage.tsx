import {useEffect, useState} from "react";
import {apiRequest, FieldConfig, StepConfig, StepForm} from "@afyaquik/shared";
import {multiSelectorWysiwygConfig} from "@afyaquik/shared/dist/StepConfig";

const TreatmentPlanAddPage = ({visitId}:{visitId:Number}) => {
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [doctorId, setDoctorId] = useState(0);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchDoctorId = async () => {
            try {
                const response = await apiRequest(`/users/me`);
                setDoctorId(response.id);
            } catch (err) {
                setError("Failed to fetch doctor information");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDoctorId();
    }, []);
    useEffect(() => {
        apiRequest('/search',{
            method: "POST",
            body:{
                searchEntity: "treatmentPlanItems",
                searchQuery: "enabled=true",
                operator: "equals",
            }
        }
        ).then((response)=>{
             const items =response.results.content.map(
                (item: { id: number; name: string; value: any }) => {
                    return {
                        name: item.name,
                        value: item.id,
                    };
                }
            )


            const config: StepConfig = {
                label: "Treatment Plan",
                fields: [],
                stepButtonLabel: "Save and go to Observation",
                topComponents: [],
                multiSelectorWysiwygConfigs: [
                    {
                        title: "Treatment Plan",
                        selectLabel: "Select Treatment Plan",
                        items: items,
                        addButtonLabel: "Add Treatment Plan",
                        selectedItemName: "treatmentPlanItemId",
                        configName: "treatmentPlanItems",
                        inputValueName: "doctorNotes"
                    } as multiSelectorWysiwygConfig
                ],
            };
            setFormConfig(config);
            }

        )
    }, [visitId]);
    return (
        <div>
            {formConfig && (
                <StepForm
                    config={[formConfig]}
                    onSubmit={(data) => {
                        console.log("Data ", data);
                        data['patientVisitId'] = visitId;
                        data['doctorId'] = doctorId;
                        console.log('Form submitted:', data);
                    }}
                    defaultValues={{}}
                    submitButtonLabel="Save Treatment Plan"
                />
            )}
        </div>
    );

}
export default TreatmentPlanAddPage;
