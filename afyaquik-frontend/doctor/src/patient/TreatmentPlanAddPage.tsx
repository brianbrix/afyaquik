import {useEffect, useState} from "react";
import {apiRequest, FieldConfig, StepConfig, StepForm} from "@afyaquik/shared";

const TreatmentPlanAddPage = ({visitId}:{visitId:Number}) => {
    const [formConfig, setFormConfig] = useState<StepConfig>();
    useEffect(() => {
        apiRequest('/search',{
            method: "POST",
            body:{
                searchEntity: "treatmentPlanItems",
            }
        }
        ).then((response)=>{
            const fields:  FieldConfig[] = response.results.content.map(
                (item: { id: number; name: string; description: string | null; }) => ({
                    name: "description",
                    label: item.name,
                    type: 'wysiwyg',
                    required: true,
                    colSpan: 6,
                })
            )
            const config: StepConfig = {
                label: "Treatment Plan",
                fields: fields,
                stepButtonLabel: "Save and go to Observation",
                topComponents: [],
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
                        data.push(
                            {
                                patientVisitId: visitId,
                                treatmentPlanItemId: 1,
                            }
                        )
                        console.log('Form submitted:', data);
                    }}
                    defaultValues={{}}
                    submitButtonLabel="Save and go to Observation"
                />
            )}
        </div>
    );

}
export default TreatmentPlanAddPage;
