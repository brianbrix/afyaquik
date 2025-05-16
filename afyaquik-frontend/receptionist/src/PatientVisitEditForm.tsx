import {apiRequest, PatientVisitForm, StepConfig} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {Button} from "react-bootstrap";


const formConfig: StepConfig[] = [
    {
        label: 'Edit Visit Info',
        fields: [
            {name: 'id',label: 'Visit Id',type:'text', disabled: true},
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
const components = function (visitId:any){
    return (
        <div className="d-flex justify-content-between">
            <Button
                variant="outline-info"
                onClick={() => window.location.href = `index.html#/patient/visits/${visitId}/details`}
            >
                Got to Visit Details
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


const PatientVisitEditForm = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log('Visit ID', id)
    formConfig.map((step) => {
        step.topComponents = [components(id)];
    });
    const [defaultValues, setDefaultValues] = useState([]);
    useEffect(() => {
        apiRequest(`/patient/visits/${id}`, { method: 'GET' })
            .then(data => {
                console.log('Visit data', data)
                setDefaultValues(data);
            })
            .catch(err => console.error(err));
    }, []);
    return (
        <PatientVisitForm formConfig={formConfig}
                      onSubmit={(data) => {
                          console.log('Submitted data:', data);
                          apiRequest(`/patients/visits/update`, { method:'PUT' , body: data})
                              .then(response => {
                                  console.log(response)
                                  window.location.href = `index.html#/patient/visits/${response.id}/details`;
                              })
                              .catch(err => console.error(err));
                      }}
                      idFromParent={id}
                      defaultValues={defaultValues}
        />
    );
}
export default PatientVisitEditForm;
