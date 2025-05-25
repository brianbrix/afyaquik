import {Edit, NumberInput, SelectInput, SimpleForm, TextInput} from 'react-admin';

const ObservationItemEdit = () => (
    <Edit>
        <SimpleForm>
            <TextInput source="id" disabled />
            <TextInput source="name" required={true} />
            <SelectInput required={true}
                         source="type"
                         choices={[
                             { id: 'SYSTEMIC', name: 'Systemic' },
                             { id: 'PHYSICAL', name: 'Physical' },
                             { id: 'OTHER', name: 'Other' }
                         ]}
            />
            <NumberInput source="price" />
        </SimpleForm>
    </Edit>
);

export default ObservationItemEdit;
