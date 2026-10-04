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
                onClick={() => window.location.href = `index.html#/visits/${visitId}/details`}
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
    const config = formConfig.map(step => ({ ...step, topComponents: [components(id)] }));
    const [defaultValues, setDefaultValues] = useState<Record<string, any> | null>(null);
    const [error, setError] = useState('');
    const [attempt, setAttempt] = useState(0);
    useEffect(() => {
        let active = true;
        setDefaultValues(null);
        setError('');
        apiRequest(`/patient/visits/${id}`, { method: 'GET' })
            .then(data => {
                if (active) setDefaultValues(data);
            })
            .catch(failure => { if (active) setError(failure.message || 'Unable to load this encounter.'); });
        return () => { active = false; };
    }, [id, attempt]);
    if (error) return <div className="alert alert-danger" role="alert">{error}<Button onClick={() => setAttempt(value => value + 1)}>Retry</Button></div>;
    if (!defaultValues) return <p role="status">Loading encounter...</p>;
    if (['COMPLETED', 'CANCELLED'].includes(defaultValues.visitStatus)) return <div className="alert alert-secondary">Closed encounters cannot be edited.{components(id)}</div>;
    return (
        <PatientVisitForm formConfig={config}
                      onSubmit={async data => {
                          const response = await apiRequest(`/patient/visits/update`, { method:'PUT', body: { ...data, id, visitStatus: undefined } });
                          window.location.href = `index.html#/visits/${response.id}/details`;
                      }}
                      idFromParent={id}
                      defaultValues={defaultValues}
                          submitButtonLabel={'Update Visit Info'}
        />
    );
}
export default PatientVisitEditForm;
