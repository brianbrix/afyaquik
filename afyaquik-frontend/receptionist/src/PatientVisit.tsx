import {apiRequest, StepConfig, StepForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";


const formConfig: StepConfig[] = [
    {
        label: 'Visit Info',
        fields: [
            { name: 'visitType', label: 'Visit Type', type: 'select', options: [
                    { label: 'Consultation', value: 'CONSULTATION' },
                    { label: 'Follow up', value: 'FOLLOW_UP' },
                    { label: 'Emergency', value: 'EMERGENCY' },
                    { label: 'Checkup', value: 'CHECKUP' },
                    { label: 'Walk In', value: 'WALK_IN' }
                ] },
            { name: 'summaryReasonForVisit', label: 'Summary', type: 'wysiwyg' },
        ],
        onStepSubmit: async (stepData,id:number | undefined) => {
            console.log('Step data:', stepData);
            console.log('ID:', id);
            const response = await apiRequest(`/patient/visits/${id}/create`, {
                method: 'POST',
                body: stepData,
            });
            return { ...stepData, id: response.id };
        }
    },
    {
        label: 'Assign Patient',
        fields: [
            { name: 'attendingPlan.patientVisitId', label: 'Visit Identifier', type: 'text', disabled: true },
            { name: 'attendingPlan.attendingOfficerUserNam', label: 'Visit Identifier', type: 'text', disabled: true },
            { name: 'attendingPlan.nextStation', label: 'Next Station', type: 'text' },
            { name: 'attendingPlan.assignedOfficer', label: 'Next Officer', type: 'text' },
        ]
    }
];
const PatientVisit = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log('Patient Id',id)
    return (

        <StepForm
            config={formConfig}
            onSubmit={(data) => {
                console.log('Submitted data:', data);
                apiRequest(`/patient/visits/${id}/create`, { method:'POST' , body: data})
                    .then(response => {
                        console.log(response)
                    })
                    .catch(err => console.error(err));
            }}
            idFromParent={id}
            defaultValues={{}}
        />);
}
export default PatientVisit;
