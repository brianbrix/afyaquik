export type FieldType = 'text' | 'email' | 'date' | 'number' | 'select' | 'checkbox';

export interface FieldConfig {
    name: string;
    label: string;
    type: FieldType;
    required?: boolean;
    hidden?: boolean;
    disabled?: boolean;
    multiple?: boolean;//for select
    options?: { label: string; value: string | number }[];
}

export interface StepConfig {
    label: string
    listUrl?:string;
    fields: FieldConfig[];
}
