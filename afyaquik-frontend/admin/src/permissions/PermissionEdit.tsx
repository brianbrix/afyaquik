import { Edit, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput, BooleanInput } from 'react-admin';

const PermissionEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="urlPattern" required={true} />
            <TextInput source="description" required={true} />
            <ReferenceArrayInput source="roleNames" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <BooleanInput source="enabled" label="Enabled" />
        </SimpleForm>
    </Edit>
);

export default PermissionEdit;
