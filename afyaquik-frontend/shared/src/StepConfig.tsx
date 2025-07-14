export type FieldType = 'text' | 'email' | 'date' | 'number' | 'select' | 'checkbox' | 'wysiwyg' | 'datetime';

export interface FieldConfig {
    name: string;
    label: string;
    type?: FieldType;
    required?: boolean;
    hidden?: boolean;
    disabled?: boolean;
    defaultValue?:any;
    onChange?: (value: any) => void;
    colSpan?:number,
    step?:string,//for number input
    value?:any,
    multiple?: boolean;//for select
    options?: { label: string; value: string | number }[];
}

export interface multiSelectorWysiwygConfig{
    title: string;
    selectLabel: string;
    items:[{name:string,value:any}]
    addButtonLabel: string;
    selectedItemName: string;
    configName:string;
    inputValueName?: string;

}
export interface StepConfig {
    label: string
    topComponents?: React.ReactNode[];
    fields: FieldConfig[];
    stepButtonLabel?: string;
    multiSelectorWysiwygConfigs?: multiSelectorWysiwygConfig[];
    onStepSubmit?: (stepData: any, id:number | undefined) => Promise<any>;

}
