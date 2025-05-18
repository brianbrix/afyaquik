import { Edit, SimpleForm, TextInput } from 'react-admin';

const StationEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" />
        </SimpleForm>
    </Edit>
);

export default StationEdit;
