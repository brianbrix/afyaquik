import { Edit, SimpleForm, TextInput } from 'react-admin';

const UserEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="username" />
            <TextInput source="firstname" />
            <TextInput source="secondname" />
            <TextInput source="lastname" />
            <TextInput source="email" />
        </SimpleForm>
    </Edit>
);

export default UserEdit;
