import { Create, SimpleForm, TextInput, SelectInput, NumberInput } from 'react-admin';

const ObservationItemCategoryCreate = () => (
    <Create>
        <SimpleForm>
            <TextInput source="name" required={true} />
            <TextInput source="description" />

        </SimpleForm>
    </Create>
);


export default ObservationItemCategoryCreate;
