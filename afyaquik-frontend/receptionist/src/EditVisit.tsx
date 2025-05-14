import {apiRequest, PatientVisit, StepConfig, StepForm} from "@afyaquik/shared";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";


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


const EditVisit = () => {
    let  params = useParams();
    const id = Number(params.id);
    console.log('Visit ID', id)
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
        <PatientVisit formConfig={formConfig}
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
export default EditVisit;
