import { Create, SimpleForm, TextInput, ReferenceArrayInput, CheckboxGroupInput,BooleanInput } from 'react-admin';

const UserCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="username" />
            <TextInput source="firstName" required={true} />
            <TextInput source="secondName" required={true} />
            <TextInput source="lastName" required={true} />
            <ReferenceArrayInput source="roles" reference="roles">
                <CheckboxGroupInput optionText="name" optionValue="name" />
            </ReferenceArrayInput>
            <TextInput source="email" required={true}/>
            <TextInput source="password" type="password" />
            <BooleanInput source="enabled" label="Enabled" />
        </SimpleForm>
    </Create>
);

export default UserCreate;
