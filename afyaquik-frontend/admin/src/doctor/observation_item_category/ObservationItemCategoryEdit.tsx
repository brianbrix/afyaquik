import {Edit, NumberInput, SelectInput, SimpleForm, TextInput} from 'react-admin';

const ObservationItemCategoryEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" required={true} />
            <TextInput source="description" />
        </SimpleForm>
    </Edit>
);

export default ObservationItemCategoryEdit;
