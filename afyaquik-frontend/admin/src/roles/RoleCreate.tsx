import { Create, SimpleForm, TextInput } from 'react-admin';

const RoleCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" />
        </SimpleForm>
    </Create>
);

export default RoleCreate;
