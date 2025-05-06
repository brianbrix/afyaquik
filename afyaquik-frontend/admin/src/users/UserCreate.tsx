import { Create, SimpleForm, TextInput } from 'react-admin';

const UserCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="username" />
            <TextInput source="firstname" />
            <TextInput source="secondname" />
            <TextInput source="lastname" />
            <TextInput source="email" />
            <TextInput source="password" type="password" />
        </SimpleForm>
    </Create>
);

export default UserCreate;
