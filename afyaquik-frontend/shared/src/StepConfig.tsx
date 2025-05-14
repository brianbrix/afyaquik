export type FieldType = 'text' | 'email' | 'date' | 'number' | 'select' | 'checkbox' | 'wysiwyg';

export interface FieldConfig {
    name: string;
    label: string;
    type?: FieldType;
    required?: boolean;
    hidden?: boolean;
    disabled?: boolean;
    onChange?:any;
    colSpan?:number,
    multiple?: boolean;//for select
    options?: { label: string; value: string | number }[];
}

export interface StepConfig {
    label: string
    listUrl?:string;
    fields: FieldConfig[];
    onStepSubmit?: (stepData: any, id:number | undefined) => Promise<any>;
}
