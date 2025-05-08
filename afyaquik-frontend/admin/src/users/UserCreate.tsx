import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const UserCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="username" />
            <TextInput source="firstName" />
            <TextInput source="secondName" />
            <TextInput source="lastName" />
            <ReferenceArrayInput source="roles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <TextInput source="email" />
            <TextInput source="password" type="password" />
            <BooleanInput source="enabled" label="Enabled" />
        </SimpleForm>
    </Create>
);

export default UserCreate;
