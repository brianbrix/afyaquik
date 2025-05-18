import { Edit, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const TriageItemEdit = () => (
    <Edit resource={"triageItems"}>
        <SimpleForm>
            <TextInput source="id" disabled={true} />
            <TextInput source="name" />
            <TextInput source="description" />
        </SimpleForm>
    </Edit>
);

export default TriageItemEdit;
