import React from 'react';
import { StepConfig } from "./StepConfig";
interface StepFormProps {
    config: StepConfig[];
    onSubmit: (data: any) => void | Promise<void>;
    defaultValues?: any;
    idFromParent?: number;
    submitButtonLabel?: string;
    bottomComponents?: React.ReactNode[];
    formMethodsRef?: React.RefObject<any>;
}
declare const StepForm: React.FC<StepFormProps>;
export default StepForm;
