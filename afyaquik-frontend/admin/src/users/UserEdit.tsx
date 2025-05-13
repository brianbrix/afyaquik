import { Edit, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const UserEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="username" />
            <TextInput source="firstName" />
            <TextInput source="secondName" />
            <TextInput source="lastName" />
            <TextInput source="email" />
            <BooleanInput source="available" />
            <ReferenceArrayInput source="roles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <ReferenceArrayInput source="stations" reference="stations">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <BooleanInput source="enabled" label="Enabled" />
        </SimpleForm>
    </Edit>
);

export default UserEdit;
