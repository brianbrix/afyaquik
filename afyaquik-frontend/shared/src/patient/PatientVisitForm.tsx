import {useParams} from "react-router-dom";
import { StepConfig } from "../StepConfig";
import  StepForm  from "../StepForm";
import React from "react";
import {Button, Card} from "react-bootstrap";
interface PatientVisitProps {
    formConfig: StepConfig[];
    onSubmit: (data: any) => void;
    idFromParent: number,
    defaultValues?: {};
    submitButtonLabel?: string;
}
export const patientName = function (id: number)
{
    const patientName = localStorage.getItem('patientName');
    if (!patientName)
        window.location.href = `index.html#/patient/${id}/details`;
    return (
        <Card className="shadow-sm">
            <Card.Header className="bg-primary text-white">
                <h5 className="mb-0">{patientName}</h5>
            </Card.Header>
        </Card>
    )
}



const PatientVisitForm: React.FC<PatientVisitProps> = ({formConfig, onSubmit, idFromParent, defaultValues, submitButtonLabel}) => {
    // let  params = useParams();
    // const id = Number(params.id);
    console.log(' Id',idFromParent)

    return (
        <>
            <StepForm
            config={formConfig}
            onSubmit={onSubmit}
            idFromParent={idFromParent}
            defaultValues={defaultValues}
            submitButtonLabel={submitButtonLabel}/>
        </>);
}
export default PatientVisitForm;
