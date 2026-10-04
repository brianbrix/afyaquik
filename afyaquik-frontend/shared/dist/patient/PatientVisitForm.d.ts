import { StepConfig } from "../StepConfig";
import React from "react";
interface PatientVisitProps {
    formConfig: StepConfig[];
    onSubmit: (data: any) => void;
    idFromParent: number;
    defaultValues?: {};
    submitButtonLabel?: string;
}
export declare const patientName: (id: number) => React.JSX.Element;
declare const PatientVisitForm: React.FC<PatientVisitProps>;
export default PatientVisitForm;
