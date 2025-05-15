import {useParams} from "react-router-dom";
import { StepConfig } from "../StepConfig";
import  StepForm  from "../StepForm";
import React from "react";
import {Card} from "react-bootstrap";
interface PatientVisitProps {
    formConfig: StepConfig[];
    onSubmit: (data: any) => void;
    idFromParent: number,
    defaultValues?: {};
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
const PatientVisit: React.FC<PatientVisitProps> = ({formConfig, onSubmit, idFromParent, defaultValues}) => {
    // let  params = useParams();
    // const id = Number(params.id);
    console.log(' Id',idFromParent)
    formConfig.map((step) => {
        step.topComponents = [patientName(idFromParent)];
    });
    return (
        <>
            <StepForm
            config={formConfig}
            onSubmit={onSubmit}
            idFromParent={idFromParent}
            defaultValues={defaultValues}
            submitButtonLabel={'Add visit'}/></>);
}
export default PatientVisit;
