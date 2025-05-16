import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const TriageItemCreate = () => (
    <Create resource={"triageItems"}>
        <SimpleForm>
            <TextInput source="name" />
            <TextInput source="description" />
        </SimpleForm>
    </Create>
);

export default TriageItemCreate;
