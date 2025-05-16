import { Create, SimpleForm, TextInput } from 'react-admin';

const StationCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" />
        </SimpleForm>
    </Create>
);

export default StationCreate;
