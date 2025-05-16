import {apiRequest, patientName, PatientVisitForm, StepConfig} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import {Button} from "react-bootstrap";
import React from "react";

const components = function (patientId:any){
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html#/patient/${patientId}/details`}
            >
                Got to Patient Details
            </Button>
            <Button
                variant="outline-success"
                onClick={() => window.location.href = `index.html`}
            >
                Go to Patients List
            </Button>
        </div>
    )
}

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
        ],
    }

];
const ReceptionPatientVisitForm = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log('Patient Id',id)
    formConfig.map((step) => {
        step.topComponents = [patientName(id),components(id)];
    });
return (
    <PatientVisitForm formConfig={formConfig}
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
export default ReceptionPatientVisitForm;
