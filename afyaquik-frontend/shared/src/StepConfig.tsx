export type FieldType = 'text' | 'email' | 'date' | 'number' | 'select' | 'checkbox' | 'wysiwyg' | 'datetime';

export interface FieldConfig {
    name: string;
    label: string;
    type?: FieldType;
    required?: boolean;
    hidden?: boolean;
    disabled?: boolean;
    onChange?: (value: any) => void;
    colSpan?:number,
    multiple?: boolean;//for select
    options?: { label: string; value: string | number }[];
}


export interface StepConfig {
    label: string
    topComponents?: React.ReactNode[];
    fields: FieldConfig[];
    onStepSubmit?: (stepData: any, id:number | undefined) => Promise<any>;
}
