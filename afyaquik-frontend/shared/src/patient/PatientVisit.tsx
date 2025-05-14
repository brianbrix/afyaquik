import {useParams} from "react-router-dom";
import { StepConfig } from "../StepConfig";
import  StepForm  from "../StepForm";
import React from "react";
interface PatientVisitProps {
    formConfig: StepConfig[];
    onSubmit: (data: any) => void;
    idFromParent: number,
    defaultValues?: {};
}

const PatientVisit: React.FC<PatientVisitProps> = ({formConfig, onSubmit, idFromParent, defaultValues}) => {
    // let  params = useParams();
    // const id = Number(params.id);
    console.log(' Id',idFromParent)
    return (

        <StepForm
            config={formConfig}
            onSubmit={onSubmit}
            idFromParent={idFromParent}
            defaultValues={defaultValues}
            submitButtonLabel={'Add visit'}
        />);
}
export default PatientVisit;
