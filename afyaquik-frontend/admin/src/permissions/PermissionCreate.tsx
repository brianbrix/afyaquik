import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput, BooleanInput } from 'react-admin';

const PermissionCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="urlPattern" required={true} />
            <TextInput source="description" required={true} />
            <ReferenceArrayInput source="roleNames" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <BooleanInput source="enabled" label="Enabled" defaultValue={true} />
        </SimpleForm>
    </Create>
);

export default PermissionCreate;
