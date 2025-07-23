import React, {useEffect, useState} from "react";
import {apiRequest, FieldConfig, StepConfig, StepForm} from "@afyaquik/shared";
import {multiSelectorWysiwygConfig} from "@afyaquik/shared/dist/StepConfig";

const TreatmentPlanAddPage = ({planId}:{planId:Number}) => {
    const [formConfig, setFormConfig] = useState<StepConfig>();
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [patientVisitId, setPatientVisitId] = useState(0);
    const [station, setStation] = useState('');
    const [loading, setLoading] = useState(true);

    const [doctorId, setDoctorId] = useState(0);
    useEffect(() => {
        if (!planId) {
            setLoading(false);
            return;
        }
        apiRequest(`/patient/visits/assignments/${planId}`, { method:'GET' })
            .then((planResponse)=>{
                console.log('Response:', planResponse);
                setPatientVisitId(planResponse.patientVisitId)
                setDoctorId(planResponse.assignedOfficerId)
                setStation(planResponse.nextStation)
            })
            .catch(error=>{
                console.error('Error:', error);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [planId]);


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
                        configName: "treatmentPlanReportItems",
                        inputValueName: "reportDetails"
                    } as multiSelectorWysiwygConfig
                ],
            };
            setFormConfig(config);
            }

        )
    }, [planId]);
    if (loading) {
        return <div>Loading form data...</div>;
    }
    return (
        <div>
            {formConfig && (
                <StepForm
                    config={[formConfig]}
                    onSubmit={(data) => {
                        data['patientVisitId'] = patientVisitId;
                        data['doctorId'] = doctorId;
                        data['station'] = station;
                        apiRequest('/plan',{
                            method: "POST",
                            body: data
                        }).then(
                            (response) => {
                                console.log("Response ", response);
                                window.location.href = `index.html#/visits/${patientVisitId}/details`;
                            }
                        ).catch((error) => {
                            console.error("Error ", error);
                        }
                        )
                    }}
                    defaultValues={{}}
                    submitButtonLabel="Save Treatment Plan"
                />
            )}
        </div>
    );

}
export default TreatmentPlanAddPage;
