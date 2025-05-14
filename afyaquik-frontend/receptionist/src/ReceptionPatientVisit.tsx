import {apiRequest, PatientVisit, StepConfig} from "@afyaquik/shared";
import {useParams} from "react-router-dom";

const formConfig: StepConfig[] = [
    {
        label: 'Visit Info',
        fields: [
            { name: 'visitType', label: 'Visit Type', type: 'select', colSpan:6 , required: true, options: [
                    { label: 'Consultation', value: 'CONSULTATION' },
                    { label: 'Follow up', value: 'FOLLOW_UP' },
                    { label: 'Emergency', value: 'EMERGENCY' },
                    { label: 'Checkup', value: 'CHECKUP' },
                    { label: 'Walk In', value: 'WALK_IN' }
                ] },
            { name: 'summaryReasonForVisit', label: 'Summary', colSpan:6 , type: 'wysiwyg' },
            {name: 'nextVisitDate', label: 'Next Visit Date', type: 'date', colSpan:6}
        ]
    }

];
const ReceptionPatientVisit = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log('Patient Id',id)
return (
    <PatientVisit formConfig={formConfig}
                  onSubmit={(data) => {
                      console.log('Submitted data:', data);
                      apiRequest(`/patients/${id}/visits/create`, { method:'POST' , body: data})
                          .then(response => {
                              console.log(response)
                              window.location.href = `index.html#/patient/visits/${response.id}/assign`;
                          })
                          .catch(err => console.error(err));
                  }}
                  idFromParent={id}
                  defaultValues={{}}
    />
)
}
export default ReceptionPatientVisit;
